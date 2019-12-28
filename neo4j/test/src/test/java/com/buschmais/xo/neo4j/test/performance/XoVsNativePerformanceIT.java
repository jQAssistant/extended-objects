package com.buschmais.xo.neo4j.test.performance;

import static com.buschmais.xo.api.Transaction.TransactionAttribute;
import static java.util.Arrays.asList;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.buschmais.xo.api.ConcurrencyMode;
import com.buschmais.xo.api.ValidationMode;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.buschmais.xo.neo4j.embedded.api.EmbeddedNeo4jDatastoreSession;
import com.buschmais.xo.neo4j.test.AbstractNeo4JXOManagerIT;
import com.buschmais.xo.neo4j.test.relation.typed.composite.TreeNode;
import com.buschmais.xo.neo4j.test.relation.typed.composite.TreeNodeRelation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.neo4j.graphdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Parameterized.class)
public class XoVsNativePerformanceIT extends AbstractNeo4JXOManagerIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(XoVsNativePerformanceIT.class);

    private static final int TREE_DEPTH = 7;
    private static final int NUMBER_OF_RUNS = 10;

    public XoVsNativePerformanceIT(XOUnit xoUnit) {
        super(xoUnit);
        LOGGER.info("Running using URI " + xoUnit.getUri().toString());
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() {
        return xoUnits(asList(TreeNode.class, TreeNodeRelation.class), Collections.emptyList(), ValidationMode.NONE, ConcurrencyMode.SINGLETHREADED,
                TransactionAttribute.NONE);
    }

    private class Measurement {

        private final long counter;
        private final long duration;
        private final double speed;

        public Measurement(long counter, long start, long stop) {
            super();
            this.counter = counter;
            this.duration = stop - start;
            speed = (counter * 1000.0) / duration;
        }

        public long getCounter() {
            return counter;
        }

        public long getDuration() {
            return duration;
        }

        public double getSpeed() {
            return speed;
        }

    }

    @Before
    public void initialize() {
        try (XOManager xoManager = getXOManagerFactory().createXOManager()) {
            EmbeddedNeo4jDatastoreSession datastoreSession = xoManager.getDatastoreSession(EmbeddedNeo4jDatastoreSession.class);
            GraphDatabaseService graphDatabaseService = datastoreSession.getGraphDatabaseService();
            Transaction transaction = graphDatabaseService.beginTx();
            Node n1 = graphDatabaseService.createNode();
            Node n2 = graphDatabaseService.createNode();
            n1.createRelationshipTo(n2, DynamicRelationshipType.withName("BOOTSTRAP"));
            transaction.success();
            transaction.close();
        }
    }

    private List<Measurement> xoMeasurements = new ArrayList<>();
    private List<Measurement> nativeMeasurements = new ArrayList<>();

    @Test
    public void test() {
        try (XOManager xoManager = getXOManagerFactory().createXOManager()) {
            nativeMeasurements = runNative(xoManager);
            xoMeasurements = runXO(xoManager);
            printResults();
        }
    }

    private List<Measurement> runXO(final XOManager xoManager) {
        ApiUnderTest<TreeNode, TreeNodeRelation> xoApi = new ApiUnderTest<TreeNode, TreeNodeRelation>() {
            @Override
            public void begin() {
                xoManager.currentTransaction().begin();
            }

            @Override
            public void commit() {
                xoManager.currentTransaction().commit();
            }

            @Override
            public TreeNode createEntity() {
                return xoManager.create(TreeNode.class);
            }

            @Override
            public void setName(TreeNode treeNode, String value) {
                treeNode.setName(value);
            }

            @Override
            public TreeNodeRelation createRelation(TreeNode parent, TreeNode child) {
                return xoManager.create(parent, TreeNodeRelation.class, child);
            }

            @Override
            public String toString() {
                return "XO";
            }
        };
        return run(xoApi);
    }

    private List<Measurement> runNative(final XOManager xoManager) {
        final GraphDatabaseService graphDatabaseService = xoManager.getDatastoreSession(EmbeddedNeo4jDatastoreSession.class).getGraphDatabaseService();
        ApiUnderTest<Node, Relationship> nativeApi = new ApiUnderTest<Node, Relationship>() {

            private Transaction transaction;

            @Override
            public void begin() {
                transaction = graphDatabaseService.beginTx();
            }

            @Override
            public void commit() {
                transaction.success();
                transaction.close();
            }

            @Override
            public Node createEntity() {
                Node node = graphDatabaseService.createNode(DynamicLabel.label(TreeNode.class.getSimpleName()));
                return node;
            }

            @Override
            public void setName(Node node, String value) {
                node.setProperty("name", value);
            }

            @Override
            public Relationship createRelation(Node parent, Node child) {
                DynamicRelationshipType relationshipType = DynamicRelationshipType.withName(TreeNodeRelation.class.getSimpleName());
                if (parent.hasRelationship(relationshipType, Direction.OUTGOING)) {
                    parent.getSingleRelationship(relationshipType, Direction.OUTGOING).delete();
                }
                Relationship relationshipTo = parent.createRelationshipTo(child, relationshipType);
                return relationshipTo;
            }

            @Override
            public String toString() {
                return "Native";
            }

        };
        return run(nativeApi);
    }

    private <Entity, Relation> List<Measurement> run(ApiUnderTest<Entity, Relation> apiUnderTest) {
        LOGGER.info("Starting run with API '" + apiUnderTest + "'");
        List<Measurement> measurements = new ArrayList<>(NUMBER_OF_RUNS);
        for (int i = 0; i < NUMBER_OF_RUNS; i++) {
            dropDatabase();
            apiUnderTest.begin();
            long start = System.currentTimeMillis();
            Entity root = apiUnderTest.createEntity();
            apiUnderTest.setName(root, "1");

            long counter = 1;
            counter += addChildren(apiUnderTest, root, 2, "1");
            apiUnderTest.commit();
            long stop = System.currentTimeMillis();

            Measurement measurement = new Measurement(counter, start, stop);
            LOGGER.info(
                    "counter=" + measurement.getCounter() + ", time=" + measurement.getDuration() + "ms" + ", speed=" + measurement.getSpeed() + " vertices/s");
            measurements.add(measurement);
        }
        LOGGER.info("Finished run with API " + apiUnderTest);
        return measurements;
    }

    private <Entity, Relation> long addChildren(ApiUnderTest<Entity, Relation> apiUnderTest, Entity parent, int i, String namePrefix) {
        if (i > TREE_DEPTH) {
            return 0;
        }
        long counter = 0;
        for (int id = 1; id <= i; id++) {
            String name = namePrefix + id;
            Entity child = apiUnderTest.createEntity();
            apiUnderTest.setName(child, name);
            apiUnderTest.createRelation(parent, child);
            counter++;
            counter += addChildren(apiUnderTest, child, i + 1, name);
        }
        return counter;
    }

    private void printResults() {
        System.out.println("===========");
        System.out.println("XO Results:");
        System.out.println("===========");
        print(xoMeasurements);
        System.out.println();
        System.out.println("===============");
        System.out.println("Native Results:");
        System.out.println("===============");
        print(nativeMeasurements);
    }

    private void print(List<Measurement> measurements) {
        System.out.println("Counter\tDuration [ms]\tSpeed [vertices/s]");
        System.out.println("--------------------------------------------------");
        long durationSum = 0;
        double speedSum = 0.0;
        for (Measurement measurement : measurements) {
            long counter = measurement.getCounter();
            long duration = measurement.getDuration();
            double speed = measurement.getSpeed();
            String format = MessageFormat.format("{0}\t{1}\t{2,number,#.##}", counter, duration, speed);
            System.out.println(format);
            speedSum += speed;
            durationSum += duration;
        }
        System.out.println("--------------------------------------------------");
        double durationAvg = (double) durationSum / (double) measurements.size();
        System.out.println(MessageFormat.format("average duration={0,number,#.##} ms", durationAvg));
        double speedAvg = speedSum / measurements.size();
        System.out.println(MessageFormat.format("average speed={0,number,#.##} vertices/s", speedAvg));
    }
}
