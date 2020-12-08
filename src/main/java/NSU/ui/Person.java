package NSU.ui;

import org.hibernate.validator.constraints.NotEmpty;

import java.util.Calendar;

public class Person {
    private Calendar created = Calendar.getInstance();

    private Long id;

    private String isSeller;

    @NotEmpty(message = "Введите ваше имя.")
    private String firstName;

    @NotEmpty(message = "Введите email.")
    private String email;

    @NotEmpty(message = "Введите номер телефона.")
    private String phone;

    @NotEmpty(message = "Введите пароль.")
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public Calendar getCreated() {
        return this.created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIsSeller() {
        return isSeller;
    }

    public void setIsSeller(String  seller) {
        isSeller = seller;
    }
}
