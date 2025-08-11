package aut.ap;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table( name = "Emails" )
public class Emails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic( optional = false )
    private String sender, recipient, subject, body, code;

    private boolean isRead;
    private LocalDate timestamp;


    public void setSender(String sender){
        this.sender = sender;
    }
    public String getSender(){
        return sender;
    }

    public void setRecipient(String recipient){
        this.recipient = recipient;
    }
    public String getRecipient(){
        return recipient;
    }

    public void setSubject(String subject){
        this.subject = subject;
    }
    public String getSubject(){
        return subject;
    }

    public void setBody(String body){
        this.body = body;
    }
    public String getBody(){
        return body;
    }

    public void setCode(String code){
        this.code = code;
    }
    public String getCode(){
        return code;
    }

    public void setRead(boolean isRead){
        this.isRead = isRead;
    }

    public void setTimestamp(LocalDate timestamp){
        this.timestamp = timestamp;
    }
    public LocalDate getTimestamp(){
        return timestamp;
    }
}
