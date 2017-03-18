package twoAK.runboyrun.response;

import java.util.Map;



public class BaseResponse {
    protected boolean success;
    protected String error_code;
    protected String error;
    protected Map<String, Object> additionalProperties;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}