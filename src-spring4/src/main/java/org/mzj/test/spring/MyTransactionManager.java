package org.mzj.test.spring;

import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

@Component
public class MyTransactionManager extends AbstractPlatformTransactionManager{
	private static final long serialVersionUID = -8372996131316516073L;

	@Override
	protected Object doGetTransaction() throws TransactionException {
		return new Object();
	}

	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
		System.out.println("transaction begin..." + transaction);
	}

	@Override
	protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
		System.out.println("transaction commit..." + status.getTransaction());
	}

	@Override
	protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
		System.out.println("transaction rollback..." + status.getTransaction());
	}
	
	@Override
	protected boolean isExistingTransaction(Object transaction) throws TransactionException {
		return super.isExistingTransaction(transaction);
	}

}
