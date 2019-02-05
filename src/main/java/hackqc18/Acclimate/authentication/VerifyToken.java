package hackqc18.Acclimate.authentication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

/**
 * Used to authenticate a user's request.
 */
public class VerifyToken {

    /**
     * Pour vérifier un IdToken provenant de l'application Android.
     * Utilise l'AdminSDK (nécessite une instance de FirebaseApp).
     *
     * @param idToken
     * @return le UID une fois le client authentifié
     *         ou 'null' s'il y a eu une erreur
     */
    public static String verifyIdToken(String idToken) {

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

            // returns the UID
            return decodedToken.getUid();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }





    // TODO: Terminer cette implémentation?

    /*
    Verify ID tokens using a third-party JWTverification library
    See:
    https://firebase.google.com/docs/auth/admin/verify-id-tokens#verify_id_tokens_using_a_third_party_jwt_library


    Verify the ID token's header conforms to the following constraints:

    ID Token Header Claims
      alg        Algorithm  "RS256"
      kid        Key ID	    Must correspond to one of the public keys listed at
    https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com


    Verify the ID token's payload conforms to the following constraints:

    ID Token Payload Claims
      exp	      Expiration time       Must be in the future. The time is measured in seconds since the UNIX epoch.
      iat	      Issued-at time        Must be in the past. The time is measured in seconds since the UNIX epoch.
      aud	      Audience              Must be your Firebase project ID, the unique identifier for your Firebase project, which can be found in the URL of that project's console.
      iss	      Issuer                Must be "https://securetoken.google.com/<projectId>", where <projectId> is the same project ID used for aud above.
      sub	      Subject               Must be a non-empty string and must be the uid of the user or device.
      auth_time	  Authentication time   Must be in the past. The time when the user authenticated.

    Finally, ensure that the ID token was signed by the private key
    corresponding to the token's kid claim. Grab the public key from
    https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com and use a JWTverification library to verify the signature. Use the value of max-age in the Cache-Control header of the response from that endpoint to know when to refresh the public keys.

    If all the above verifications are successful, you can use the subject
    (sub) of the ID token as the uid of the corresponding user or device.
     */

//    /**
//     * Pour une vérification effectuée sans l'aide de l'API Admin SDK.
//     *
//     * Utilise la librairie "auth0".
//     * https://github.com/auth0/java-jwt
//     *
//     * @param idToken
//     * @return
//     */
//    public static String verifyIdTokenWithLibrary(String idToken) {
//
//        RSAPublicKey publicKey = bob; //Get the key instance
//        RSAPrivateKey privateKey = bob; //Get the key instance
//
//        try {
//            Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
//            JWTVerifier verifier = JWTverification.require(algorithm)
//                    .withIssuer("auth0")
//                    .build(); //Reusable verifier instance
//            DecodedJWT jwt = verifier.verify(idToken);
//
//        } catch (JWTVerificationException exception){
//            //Invalid signature/claims
//        }
//
//        return "";
//    }
}
