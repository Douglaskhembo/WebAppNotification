//package AppWeb.AppWeb.model;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "application")
//public class Application {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String name;
//    private String version;
//    private String publisher;
//    private String installationDate;
//
//    public Application(String name, String version, String publisher, String installationDate) {
//        this.name = name;
//        this.version = version;
//        this.publisher = publisher;
//        this.installationDate = installationDate;
//    }
//
//    public Application() {
//
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getVersion() {
//        return version;
//    }
//
//    public void setVersion(String version) {
//        this.version = version;
//    }
//
//    public String getPublisher() {
//        return publisher;
//    }
//
//    public void setPublisher(String publisher) {
//        this.publisher = publisher;
//    }
//
//    public String getInstallationDate() {
//        return installationDate;
//    }
//
//    public void setInstallationDate(String installationDate) {
//        this.installationDate = installationDate;
//    }
//}
//
