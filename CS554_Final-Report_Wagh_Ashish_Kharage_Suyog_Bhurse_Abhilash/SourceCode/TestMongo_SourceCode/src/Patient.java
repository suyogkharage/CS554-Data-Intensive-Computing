import java.util.Date;

public class Patient {
	public double patientId;
    public String patientName;
    public Date patientDOB;
    public String patientAddr;
    public String hospitalName;
    public String condition;
    public String treatment;
    public String description;
    
    public Patient() {
    	
    }
    
	
	public double getPatientId() {
		return patientId;
	}
	public void setPatientId(double patientId) {
		this.patientId = patientId;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public Date getPatientDOB() {
		return patientDOB;
	}
	public void setPatientDOB(Date patientDOB) {
		this.patientDOB = patientDOB;
	}
	public String getPatientAddr() {
		return patientAddr;
	}
	public void setPatientAddr(String patientAddr) {
		this.patientAddr = patientAddr;
	}
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getTreatment() {
		return treatment;
	}
	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
    
}
