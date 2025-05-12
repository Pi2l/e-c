mkdir -p ./src/main/resources/keys

openssl genpkey -algorithm RSA -out private_key.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in private_key.pem -out public_key.pem

mv private_key.pem ./src/main/resources/keys/private_key.pem
mv public_key.pem ./src/main/resources/keys/public_key.pem