
package com.tcs.sgv.dcps.dao;

import java.util.Date;
import java.util.List;

import com.tcs.sgv.core.dao.GenericDao;


public interface NpsFileStatusChangeDAO extends GenericDao {

        public List getFinyear();

        void insertFileDetails(Long lLngPkIdForFile,String fileArray,String reasonArray,Long gLngPostId,String remarksArray,String empContributionArray,String emplyrContributionArray,String totalFinalAmountArray,String txtMonth,String txtYear); 

        String updateFileStatus(String fileName);
}
