package jp.co.polarify.onboarding.app.apilogger;

public class LogModel {
    String[] keywords;
    String className ;
    String error_message;
    String function_name;
    String log;

    public LogModel(String[] keywords, String className , String error_message, String function_name, String log) {
        this.keywords = keywords;
        this.className = className;
        this.error_message = error_message;
        this.function_name = function_name;
        this.log = log;
    }


}
