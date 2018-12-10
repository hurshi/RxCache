package victoralbertos.io.android;

public class CurTime {
    private int success;
    private Result result;

    public CurTime(int success, Result result) {
        this.success = success;
        this.result = result;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public class Result {
        private String timestamp;
        private String datetime_1;
        private String datetime_2;
        private String week_1;
        private String week_2;
        private String week_3;
        private String week_4;

        public Result(String timestamp, String datetime_1, String datetime_2, String week_1, String week_2, String week_3, String week_4) {
            this.timestamp = timestamp;
            this.datetime_1 = datetime_1;
            this.datetime_2 = datetime_2;
            this.week_1 = week_1;
            this.week_2 = week_2;
            this.week_3 = week_3;
            this.week_4 = week_4;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getDatetime_1() {
            return datetime_1;
        }

        public void setDatetime_1(String datetime_1) {
            this.datetime_1 = datetime_1;
        }

        public String getDatetime_2() {
            return datetime_2;
        }

        public void setDatetime_2(String datetime_2) {
            this.datetime_2 = datetime_2;
        }

        public String getWeek_1() {
            return week_1;
        }

        public void setWeek_1(String week_1) {
            this.week_1 = week_1;
        }

        public String getWeek_2() {
            return week_2;
        }

        public void setWeek_2(String week_2) {
            this.week_2 = week_2;
        }

        public String getWeek_3() {
            return week_3;
        }

        public void setWeek_3(String week_3) {
            this.week_3 = week_3;
        }

        public String getWeek_4() {
            return week_4;
        }

        public void setWeek_4(String week_4) {
            this.week_4 = week_4;
        }
    }
}
