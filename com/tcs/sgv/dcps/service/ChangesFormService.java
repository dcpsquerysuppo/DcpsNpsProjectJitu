/* Decompiler 6ms, total 767ms, lines 27 */
package com.tcs.sgv.dcps.service;

import com.tcs.sgv.core.valueobject.ResultObject;
import java.util.Map;

public interface ChangesFormService {
   ResultObject loadChangesForm(Map var1);

   ResultObject updatePersonalDtls(Map var1);

   ResultObject updateOfficeDtls(Map var1);

   ResultObject updateOtherDtls(Map var1);

   ResultObject updateNomineeDtls(Map var1);

   ResultObject updatePhotoAndSignDtls(Map var1);

   ResultObject forwardChangesToDDO(Map var1);

   ResultObject loadChangesDrafts(Map var1);

   ResultObject rejectChangesToDDOAsst(Map var1);

   ResultObject approveChangesByDDO(Map var1);
}