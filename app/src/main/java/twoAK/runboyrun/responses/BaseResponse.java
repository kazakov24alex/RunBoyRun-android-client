package twoAK.runboyrun.responses;

import java.util.Map;


/** Base response */
public class BaseResponse {
    protected boolean success;      // success of response
    protected String error_code;    // code of error
    protected String error;         // message of error
    protected Map<String, Object> additionalProperties; // individual properties of a response


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