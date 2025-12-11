# EasyAuth — Local Sessions

A Minecraft 1.20.1 Fabric mod that adds locally stored sessions for automatic authorization in EasyAuth by NikitaCartes mod to provide seamless login without password for offline players.

## Features

- Seamless authorization per request by server
- Stores authorization token when authentication was successfull
- Fallback to usual behaviour if server does not accept authorization token (this patch is not installed on server/authorization token is invalid)
- Authorization tokens stored locally are encrypted with a key derived from IP address of the server (AES-GCM-256)
- Authorization tokens are further protected from malware on device by hashing derived key before saving to an encrypted file
- Generated authorization tokens stored on server are hashed to mitigate filesystem breach attack
