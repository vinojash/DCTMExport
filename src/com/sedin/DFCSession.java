package com.sedin;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.IDfLoginInfo;

public class DFCSession {
	private String repository = null;
	private String userName = null;
	private String password = null;

	public DFCSession() throws Exception {
		this.repository = "corp";
		this.userName = "Administrator";
		this.password = "Sedin@123";
	}

	public IDfSession createSession() throws Exception {
		IDfClientX clientx = new DfClientX();
		IDfClient client = clientx.getLocalClient();
		IDfSessionManager sMgr = client.newSessionManager();
		IDfLoginInfo loginInfoObj = clientx.getLoginInfo();
		loginInfoObj.setUser(userName);
		loginInfoObj.setPassword(password);
		loginInfoObj.setDomain(null);
		sMgr.setIdentity(repository, loginInfoObj);
		System.err.println("Session Created..!");
		return sMgr.getSession(repository);
	}

	public void releaseSession(IDfSession session) throws Exception {
		if (session != null) {
			IDfSessionManager sMgr = session.getSessionManager();
			sMgr.release(session);
			System.err.println("Session Released..!");
		}
	}

}
