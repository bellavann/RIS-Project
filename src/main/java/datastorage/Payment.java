package datastorage;

public class Payment {

    String appointmentID;
    String time;
    float payment;
    int byPatient;

    public Payment(String appointmentID, String time, float payment) {
        this.appointmentID = appointmentID;
        this.time = time;
        this.payment = payment;
    }

    public int getByPatient() {
        return byPatient;
    }

    public void setByPatient(int byPatient) {
        this.byPatient = byPatient;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getPayment() {
        return payment;
    }

    public void setPayment(float payment) {
        this.payment = payment;
    }

}
