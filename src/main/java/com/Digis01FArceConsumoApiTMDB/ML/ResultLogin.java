package com.Digis01FArceConsumoApiTMDB.ML;

public class ResultLogin {
    private Boolean sucess;
    private String status_code;
    private String status_message;

    public Boolean getSucess(){
        return sucess;
    }
    
    public void setSucess(Boolean sucess){
        this.sucess = sucess;
    }
    
    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }
    
    public String getStatus_message(){
        return status_message;
    }
    
    public void setStatus_message(String status_message){
        this.status_message = status_message;
    }
}
