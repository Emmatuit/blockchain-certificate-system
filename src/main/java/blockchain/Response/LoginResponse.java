package blockchain.Response;

public class LoginResponse {
    public static class Builder {
        private String token;
        private String role;

        public LoginResponse build() {
            return new LoginResponse(this);
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }
    }
    public static Builder builder() {
        return new Builder();
    }

    private String token;

    private String role;
    
	public LoginResponse() {}

    private LoginResponse(Builder builder) {
        this.token = builder.token;
        this.role = builder.role;
    }

    public String getRole() {
		return role;
	}


    public String getToken() {
        return token;
    }
}