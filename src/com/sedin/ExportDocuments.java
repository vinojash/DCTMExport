package com.sedin;

import java.io.File;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.operations.IDfExportNode;
import com.documentum.operations.IDfExportOperation;

public class ExportDocuments {

	private static final String localFilePath = "C:\\temp\\destination";
	private static final String dql = "select r_object_id from dm_document order by r_creation_date desc enable (return_top 1000);";

	// Generic Method for Initializing the session manager
	public static void main(String[] args) throws Exception {
		DFCSession dFCSession = new DFCSession();
		IDfSession mySession = dFCSession.createSession();
		try {
			executeQuery(mySession);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dFCSession.releaseSession(mySession);
		}
	}

	public static void export(IDfSession session, String objectId) {
		IDfSysObject sysObj = null;
		try {
			sysObj = (IDfSysObject) session.getObject(new DfId(objectId));
			IDfExportOperation operation = new DfClientX().getExportOperation();
			operation.setDestinationDirectory(localFilePath);
			IDfExportNode node = (IDfExportNode) operation.add(sysObj);
			node.setFormat(sysObj.getFormat().getName());
			String docExt = sysObj.getFormat().getString("dos_extension");
			boolean isExported = operation.execute();
			if (isExported) {
				String fileName = node.getFilePath();
				rename(fileName, objectId, docExt);
			}
		} catch (DfException e) {
			e.printStackTrace();
		}
	}

	private static void rename(String fileName, String objectId, String docExt) {
		File f1 = new File(fileName);
		File f2 = new File(localFilePath + "\\" + objectId + "." + docExt);
		boolean b = f1.renameTo(f2);
		if (b) {
			System.out.println("File renamed successfully..!");
		} else {
			System.out.println("File name failed..!");
		}
		System.out.println("Before : " + f1.getAbsolutePath() + "\nAfter : " + f2.getAbsolutePath());
	}

	public static void executeQuery(IDfSession session) throws DfException {
		IDfClientX clientx = new DfClientX();

		IDfQuery query = clientx.getQuery();
		query.setDQL(dql);
		IDfCollection collection = null;
		collection = query.execute(session, IDfQuery.READ_QUERY);
		while (collection.next()) {
			String objectId = collection.getString("r_object_id");
			export(session, objectId);
		}
		if (null != collection) {
			collection.close();
		}

	}
}
