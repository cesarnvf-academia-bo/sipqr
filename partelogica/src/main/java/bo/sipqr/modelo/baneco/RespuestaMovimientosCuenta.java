package bo.sipqr.modelo.baneco;

import java.util.ArrayList;
import java.util.List;

public class RespuestaMovimientosCuenta extends RespuestaBaneco {
    private AccountHeader accountHeader;
    private List<AccountDetail> accountDetailList = new ArrayList<AccountDetail>();
    private List<AccountWithheld> accountWithheldList = new ArrayList<AccountWithheld>();

    public AccountHeader getAccountHeader() {
        return accountHeader;
    }

    public void setAccountHeader(AccountHeader accountHeader) {
        this.accountHeader = accountHeader;
    }

    public List<AccountDetail> getAccountDetailList() {
        return accountDetailList;
    }

    public void setAccountDetailList(List<AccountDetail> accountDetailList) {
        this.accountDetailList = accountDetailList;
    }

    public List<AccountWithheld> getAccountWithheldList() {
        return accountWithheldList;
    }

    public void setAccountWithheldList(List<AccountWithheld> accountWithheldList) {
        this.accountWithheldList = accountWithheldList;
    }
}
