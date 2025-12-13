# EasyAuth — Local Sessions

[Download from modrinth](https://modrinth.com/mod/easy-auth-local-sessions) · [Download from git.hloth.dev](https://git.hloth.dev/demovio/easy-auth-local-sessions/releases)

A Minecraft 1.20.1 Fabric patching (mixin) mod that adds locally stored sessions for automatic authorization in EasyAuth
by NikitaCartes mod to provide seamless login without password for offline players.

<video src="./docs/demo.mp4" width="755" height="413"></video>

## Features

- Client: Seamless authorization per request by server
- Client: Stores generated authorization token after authenticating with password for the first time
- Client: Fallback to usual behaviour if server does not accept authorization token (e.g. this patch is not installed on
  server or authorization token is invalid)
- Client: Authorization tokens stored locally are encrypted with a key derived from player UUID and IP address of the
  server (AES-GCM-256)
- Client: Authorization tokens are further protected by hashing the derived key to protect them from malware on device
- Server: Generated authorization tokens are hashed to mitigate filesystem breach attack

Client-side saved authorization tokens (for servers you join) are stored in config/EasyAuthLocalSessions-client/
directory

Server-side saved authorization tokens (for players joining your server) are stored in
EasyAuth/EasyAuthLocalSessions-server

## Install

Server:

1. Download this mod to server
2. (*optional* but **HIGHLY RECOMMENDED!**) Set `session-timeout` to `-1` in config/EasyAuth/main.conf to disable IP
   authorization (which is insecure and unneeded with this mod)

Client:

For obvious reasons, don't install this mod to guest computers or type `/logout` when you're leaving it

1. Download this mod to client
2. Join the server and login with your password for the first and only time
3. From now on, every time you're joining the server (and server prompts to authorize) the mod will silently send
   authorization token
4. If you want to stop this behaviour, type `/logout` on the server you want to log out from
5. If you believe your authorization token was
   compromised, [changing password](https://github.com/NikitaCartes/EasyAuth/wiki) revokes all authorization tokens on
   the server for your account

Important caveats:

- Locally stored authorization tokens are tied to the exact server address (see
  issue [#1](https://git.hloth.dev/demovio/easy-auth-local-sessions/issues/1)) and player UUID (derived
  from
  name for offline players)
- If a server changes IP address, port, domain, you'll have to authorize again. Even if you join `example.org:25565`
  instead of `example.org` it will be considered a separate server with separate tokens (
  see [issue #1](https://git.hloth.dev/demovio/easy-auth-local-sessions/issues/1)). And vice
  versa, if someone hosts a server under the same domain or IP address, the mod will send the authorization token to it,
  which can then be used in replay attacks.
  See [issue #2](https://git.hloth.dev/demovio/easy-auth-local-sessions/issues/2)

## License

[MIT](./LICENSE)

## Donate

[hloth.dev/donate](https://hloth.dev/donate)