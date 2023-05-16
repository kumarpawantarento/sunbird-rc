const urlPath = "/certificate";
const registerMemberLimit = 4;
const certificatePublicKey = process.env.CERTIFICATE_PUBLIC_KEY || "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnXQalrgztecTpc+INjRQ8s73FSE1kU5QSlwBdICCVJBUKiuQUt7s+Z5epgCvLVAOCbP1mm5lV7bfgV/iYWDio7lzX4MlJwDedWLiufr3Ajq+79CQiqPaIbZTo0i13zijKtX7wgxQ78wT/HkJRLkFpmGeK3za21tEfttytkhmJYlwaDTEc+Kx3RJqVhVh/dfwJGeuV4Xc/e2NH++ht0ENGuTk44KpQ+pwQVqtW7lmbDZQJoOJ7HYmmoKGJ0qt2hrj15uwcD1WEYfY5N7N0ArTzPgctExtZFDmituLGzuAZfv2AZZ9/7Y+igshzfB0reIFdUKw3cdVTzfv5FNrIqN5pwIDAQAB\n-----END PUBLIC KEY-----\n"
const certificatePublicKeyBase58 = process.env.CERTIFICATE_PUBLIC_KEY_BASE58 ||"DaipNW4xaH2bh1XGNNdqjnSYyru3hLnUgTBSfSvmZ2hi";

const CERTIFICATE_CONTROLLER_ID = process.env.REACT_APP_CERTIFICATE_CONTROLLER_ID || 'https://sunbird.org/';
const CERTIFICATE_NAMESPACE = process.env.REACT_APP_CERTIFICATE_NAMESPACE || "https://cvstatus.icmr.gov.in/credentials/testCertificate/v1";
const CERTIFICATE_PUBKEY_ID = process.env.REACT_APP_CERTIFICATE_PUBKEY_ID || 'https://cvstatus.icmr.gov.in/i/india';
const CERTIFICATE_DID = process.env.REACT_APP_CERTIFICATE_DID || 'did:india';
const CERTIFICATE_SCAN_TIMEOUT = process.env.REACT_APP_CERTIFICATE_SCAN_TIMEOUT || '45000';
const CERTIFICATE_SIGNED_KEY_TYPE = process.env.CERTIFICATE_SIGNED_KEY_TYPE || 'ED25519';

module.exports = {
  urlPath,
  certificatePublicKey,
  registerMemberLimit,
  CERTIFICATE_CONTROLLER_ID,
  CERTIFICATE_DID,
  CERTIFICATE_NAMESPACE,
  CERTIFICATE_PUBKEY_ID,
  CERTIFICATE_SCAN_TIMEOUT,
  CERTIFICATE_SIGNED_KEY_TYPE,
  certificatePublicKeyBase58
};
