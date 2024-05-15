package ch.uzh.ifi.hase.soprafs24.websocket;

public class TimeDTO {
    private String time;

    public TimeDTO(String s) {
        this.time = s;
    }

    public String getTime(){return time;}

    public void setTime(String time){
        this.time = time;
    }
}
