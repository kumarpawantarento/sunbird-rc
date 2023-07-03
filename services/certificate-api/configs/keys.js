const publicKeyPem = process.env.CERTIFICATE_PUBLIC_KEY || '';
// eslint-disable-next-line max-len
const privateKeyPem = process.env.CERTIFICATE_PRIVATE_KEY || '';
const qrType = 'URL';
const certDomainUrl = process.env.CERTIFICATE_DOMAIN_URL || "http://10.0.2.18:8082";
const smsAuthKey = "";
module.exports = {
  publicKeyPem,
  privateKeyPem,
  smsAuthKey,
  qrType,
  certDomainUrl
};

/*
// openssl genrsa -out key.pem; cat key.pem;
// openssl rsa -in key.pem -pubout -out pubkey.pem;
// cat pubkey.pem; rm key.pem pubkey.pem
*/