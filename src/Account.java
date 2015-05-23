import java.io.Serializable;

public class Account implements Serializable {
    private String usrname_;
    private String pswd_;
    private boolean isOnline_ = false;
    
    public void setOnline(boolean arg) 
    { isOnline_ = arg; } 
    
    public boolean isOnline() 
    { return isOnline_; } 
    
    public void setUsrname(String arg) 
    { usrname_ = arg; }
    
    public String getUsrname() 
    { return usrname_; }
    
    public void setPswd(String arg)
    { pswd_ = arg; }

    public String getPswd() 
    { return pswd_; }
}
