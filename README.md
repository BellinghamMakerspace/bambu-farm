Since you are deploying this for the Bellingham Makerspace organization, the README needs to balance clear "Makerspace" instructions with the Cybersecurity student hardening you've implemented.

Here is a comprehensive, production-ready README.md that incorporates your SSL redirects, AES encryption, and troubleshooting steps.
🖨️ Bambu Farm (Hardened Edition)

A secure, web-based dashboard to monitor and manage multiple Bambu Lab printers (A1, P1, X1 series) over a local network or cloud.
🚀 Quick Start (Fedora/Linux)
1. Prerequisites

    Java 21 LTS (Required for Quarkus 3.x)

    SSL Certificates: cert.pem and key.pem must be in the root directory.

    Fedora Users: Run sudo update-ca-trust after adding your cert to anchors to fix Vaadin Push warnings.

2. Installation & Build
Bash

git clone https://github.com/BellinghamMakerspace/bambu-farm.git
cd bambu-farm
mvn clean install -Pproduction

3. Secure Launch

Do not run the JAR directly. Use the provided launcher to ensure SSL and Config priorities are handled:
Bash

chmod +x start-farm.sh
./start-farm.sh

🛡️ Cybersecurity Hardening

This fork of Bambu Farm includes critical security upgrades implemented for production environments:

    AES-256 at Rest: The H2 database (bambu-users.mv.db) is encrypted using AES. Without the specific CIPHER key in your .env, the database is an unreadable binary blob.

    Enforced SSL Redirect: All traffic on 8080 is automatically redirected to the secure 8443 port.

    Progressive Tarpitting: The login system includes a rate-limiting delay that increases exponentially on failed attempts, effectively neutralizing brute-force attacks.

    Bcrypt Hashing: User passwords are never stored in plain text; only high-entropy Bcrypt hashes are permitted in the configuration.

⚙️ Configuration (.env)

Create a .env file in the root directory. Note: If you change encryption keys, you must delete the existing .mv.db file.
Properties

# --- NETWORK ---
quarkus.http.host=0.0.0.0
quarkus.http.port=8080

# --- DATABASE (AES HARDENING) ---
QUARKUS_DATASOURCE_JDBC_URL=jdbc:h2:file:./bambu-users;CIPHER=AES
QUARKUS_DATASOURCE_PASSWORD=YourSecureSecretKey password123

# --- ADMIN CREDENTIALS ---
# Password: admin (Bcrypt hashed)
bambu.users.admin.password=$2a$10$0tygFgl.x3QZR9Az9TYcmu0ip22PqYi5r8QPx.nubrZVYtODy9vsGI
bambu.users.admin.role=admin

# --- PRINTERS ---
bambu.printers.p1s_lab.device-id=SERIAL_HERE
bambu.printers.p1s_lab.access-code=CODE_HERE
bambu.printers.p1s_lab.ip=192.168.1.XXX

🛠️ Troubleshooting
"Push will not work" (Vaadin Error)

This occurs when the browser blocks the WebSocket (wss://) handshake due to a self-signed certificate.
Fix (Fedora):

    sudo cp cert.pem /etc/pki/ca-trust/source/anchors/bambu.crt

    sudo update-ca-trust

    Restart your browser.

"Port 8080 in use"

The start-farm.sh script automatically attempts to kill processes on 8080. If it fails:
Bash

sudo fuser -k 8080/tcp

"Store header is corrupt"

This happens if you try to open an unencrypted database with an AES-enabled config.
Fix: rm bambu-users.mv.db and restart.
🤝 Contributing

    Fork the repository to the BellinghamMakerspace organization.

    Create a feature branch (git checkout -b feature/cool-new-thing).

    Commit your changes and push to your fork.

    Open a Pull Request against the main branch.

Would you like me to generate a CONTRIBUTING.md file specifically for the Makerspace members to go along with this README?

I want to include the orginal readme and warnings

This README combines the original project documentation and warnings with the specific Cybersecurity Hardening and Fedora-specific fixes we've implemented for the Bellingham Makerspace.
🖨️ Bambu Farm (Makerspace Edition)

A secure, web-based dashboard to monitor and manage multiple Bambu Lab printers (A1, P1, X1 series) using MQTT / FTP / RTSP.
⚠️ CRITICAL WARNINGS (Original Project)

    [!IMPORTANT]

    Firmware Blockade: Bambu Lab has started blocking printing via MQTT unless LAN Mode is enabled. If you cannot print, consider downgrading your firmware or enabling Cloud Mode in the config.

    X1C Compatibility: FTPS connections for the X1C require SSL Session Reuse. Ensure bambu.use-bouncy-castle=true is set in your .env and the JVM flag -Djdk.tls.useExtendedMasterSecret=false is used.

🛡️ CYBERSECURITY HARDENING (Makerspace Fork)

This version includes specific security enhancements developed for the Makerspace environment:

    AES-256 at Rest: The user database (bambu-users.mv.db) is encrypted at the file level. If the server is stolen or the file is exfiltrated, user credentials remain unreadable.

    Progressive Tarpitting: Brute-force attacks are neutralized by an exponential delay (tarpit) on failed login attempts.

    Enforced SSL: All HTTP (8080) traffic is automatically redirected to HTTPS (8443).

    Bcrypt Hashing: No plain-text passwords are stored in the configuration; all credentials use high-entropy Bcrypt hashes.

🚀 Installation & Build
1. Prerequisites

    Java 21 LTS (OpenJDK/Zulu/Temurin)

    Maven (For building from source)

    SSL Certificates: cert.pem and key.pem must be present in the root folder.

2. Build from Source
Bash

mvn clean install -Pproduction

3. Fedora/Linux Certificate Trust

To stop the browser from blocking real-time updates (Vaadin Push), you must trust your self-signed cert:
Bash

sudo cp cert.pem /etc/pki/ca-trust/source/anchors/bambu.crt
sudo update-ca-trust

⚙️ Configuration (.env)

Create an .env file in the root directory. Note: If you change the AES password, you must delete the old .mv.db file.
Properties

# --- SERVER ---
quarkus.http.host=0.0.0.0
quarkus.http.port=8080

# --- DATABASE (AES HARDENING) ---
QUARKUS_DATASOURCE_JDBC_URL=jdbc:h2:file:./bambu-users;CIPHER=AES
QUARKUS_DATASOURCE_PASSWORD=YourMakerspaceSecretKey password123

# --- ADMIN CREDENTIALS ---
# Password: admin (Bcrypt hashed)
bambu.users.admin.password=$2a$10$0tygFgl.x3QZR9Az9TYcmu0ip22PqYi5r8QPx.nubrZVYtODy9vsGI
bambu.users.admin.role=admin

# --- PRINTERS ---
bambu.printers.p1s_lab.device-id=SERIAL_NUMBER
bambu.printers.p1s_lab.access-code=ACCESS_CODE
bambu.printers.p1s_lab.ip=192.168.1.XXX

🛠️ Execution

Use the included start-farm.sh script. This handles port conflicts (killing ghosts on 8080) and injects the necessary SSL properties into the JVM.
Bash

chmod +x start-farm.sh
./start-farm.sh

Feature Support Matrix
Feature	A1	P1P/S	X1C
Remote View	[x]	[x]	[x]
SD Upload	[x]	[x]	[x]²
AMS Support	?	[x]	[x]
🤝 Contributing

    Fork this repository to the BellinghamMakerspace organization.

    Create a feature branch (git checkout -b feature/new-logic).

    Ensure all security hardening tests pass (try a curl brute-force loop).

    Open a Pull Request.
