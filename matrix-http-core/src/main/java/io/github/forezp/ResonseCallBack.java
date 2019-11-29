package io.github.forezp;

public interface ResonseCallBack {

    void completed(int httpCode, String result);

    void failed(Exception e);

    class DEAULT implements ResonseCallBack {
        private int httpCode;
        private String data;
        private Exception exception;

        @Override
        public void completed(int httpCode, String data) {
            this.httpCode = httpCode;
            this.data = data;

        }

        @Override
        public void failed(Exception e) {
            this.exception = exception;
        }

        public int getHttpCode() {
            return httpCode;
        }

        public String getData() {
            return data;
        }

        public Exception getException() {
            return exception;
        }
    }
}
