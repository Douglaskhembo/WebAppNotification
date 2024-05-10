package AppWeb.AppWeb.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WindowsApplicationManager {

    public static List<String> getInstalledApplications() {
        List<String> installedApplications = new ArrayList<>();

        try {
            // PowerShell script to retrieve installed application names
            // String script = "Get-ItemProperty HKLM:\\Software\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\* | Select-Object DisplayName";
            String script ="(Get-AppxPackage | Select-Object Name).Name";

            //String script = "(Get-ItemProperty HKLM:\\Software\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\* | Select-Object DisplayName).DisplayName; " +
                    //"(Get-AppxPackage | Select-Object Name).Name";

            // Command to execute PowerShell script
            String command = "powershell.exe -Command \"" + script + "\"";

            // Create ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // Assuming each line contains the name of an installed application
                installedApplications.add(line.trim());
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();

            // Log exit code
            System.out.println("Process exit code: " + exitCode);

            // If exit code is not 0, log error
            if (exitCode != 0) {
                System.err.println("PowerShell command failed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred while retrieving installed applications:");
            e.printStackTrace();
        }

        // Log the contents of installedApplications before returning
        System.out.println("Installed Applications: " + installedApplications);
        return installedApplications;
    }
}
