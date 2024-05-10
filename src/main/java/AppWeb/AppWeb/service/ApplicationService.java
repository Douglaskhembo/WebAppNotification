package AppWeb.AppWeb.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    public List<String> getInstalledApplications() {
        // Fetch the list of installed applications using WindowsApplicationManager
        return WindowsApplicationManager.getInstalledApplications();
    }
}
