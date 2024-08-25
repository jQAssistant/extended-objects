package com.buschmais.xo.spring;

import com.buschmais.xo.api.XOManager;

import lombok.ToString;
import org.springframework.transaction.support.ResourceHolderSupport;
import org.springframework.util.Assert;

@ToString
public class XOManagerHolder extends ResourceHolderSupport {

    private final XOManager xoManager;

    public XOManagerHolder(XOManager xoManager) {
        Assert.notNull(xoManager, "XOManager must not be null");
        this.xoManager = xoManager;
    }

    public XOManager getXOManager() {
        return this.xoManager;
    }

    protected boolean isTransactionActive() {
        return this.xoManager.currentTransaction()
            .isActive();
    }

}
