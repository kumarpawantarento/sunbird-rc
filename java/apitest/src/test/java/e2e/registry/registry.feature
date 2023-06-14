
Feature: Registry api tests
  Background:
    * string registryUrl = "http://localhost:8081"
    * string authUrl = "http://localhost:8080"
    * url registryUrl
    * def admin_token = ""
    * def client_secret = 'a52c5f4a-89fd-40b9-aea2-3f711f14c889'
    * def sleep = function(millis){ java.lang.Thread.sleep(millis) }

  Scenario: health check
    Given path 'health'
    When method get
    Then status 200

  Scenario: Create birth certificate schema and issue credentials
#    get admin token
    * url authUrl
    * path 'auth/realms/sunbird-rc/protocol/openid-connect/token'
    * header Content-Type = 'application/x-www-form-urlencoded; charset=utf-8'
    * header Host = 'keycloak:8080'
    * form field grant_type = 'client_credentials'
    * form field client_id = 'admin-api'
    * form field client_secret = client_secret
    * method post
    Then status 200
    And print response.access_token
    * def admin_token = 'Bearer ' + response.access_token
# create birth certificate schema
    Given url registryUrl
    And path 'api/v1/Schema'
    And header Authorization = admin_token
    And request read('BirthCertificateSchemaRequest.json')
    When method post
    Then status 200
    And response.params.status == "SUCCESSFUL"
# create entity for birth certificate
    Given url registryUrl
    And path 'api/v1/BirthCertificate'
    And request read('BirthCertificateRequest.json')
    When method post
    Then status 200
    And def birthCertificateOsid = response.result.BirthCertificate.osid
  # get entity by id
    Given url registryUrl
    And path 'api/v1/BirthCertificate/' + birthCertificateOsid
    And header Authorization = admin_token
    When method get
    Then status 200
    And response._osSignedData.length > 0
  # modify entity
    Given url registryUrl
    And path 'api/v1/BirthCertificate/' + birthCertificateOsid
    And header Authorization = admin_token
    * def requestBody = read('BirthCertificateRequest.json')
    * requestBody.name = "test"
    And request requestBody
    When method put
    Then status 200
    And response.params.status == "SUCCESSFUL"
  # get entity by id
    Given url registryUrl
    And path 'api/v1/BirthCertificate/' + birthCertificateOsid
    And header Authorization = admin_token
    When method get
    Then status 200
    And response.name == "test"
    And response._osSignedData.contains("test")
  # get certificate for entity
    Given url registryUrl
    And path 'api/v1/BirthCertificate/' + birthCertificateOsid
    And header Authorization = admin_token
    And header Accept = "text/html"
    And header template-key = "html"
    When method get
    Then status 200
    And response.length > 0
  # get VC for entity
    Given url registryUrl
    And path 'api/v1/BirthCertificate/' + birthCertificateOsid
    And header Authorization = admin_token
    And header Accept = "application/vc+ld+json"
    When method get
    Then status 200
    And response.credentialSubject.name == "test"
  # delete entity by id
    Given url registryUrl
    And path 'api/v1/BirthCertificate/' + birthCertificateOsid
    And header Authorization = admin_token
    When method delete
    Then status 200
    And response.name == "test"
    And response.params.status == "SUCCESSFUL"
  # get entity by id
    Given url registryUrl
    And path 'api/v1/BirthCertificate/' + birthCertificateOsid
    And header Authorization = admin_token
    When method get
    Then status 404
    And response.params.status == "UNSUCCESSFUL"
    And response.params.errmsg == "entity status is inactive"


  Scenario: Create student schema and verify crud apis
  #    get admin token
    * url authUrl
    * path 'auth/realms/sunbird-rc/protocol/openid-connect/token'
    * header Content-Type = 'application/x-www-form-urlencoded; charset=utf-8'
    * header Host = 'keycloak:8080'
    * form field grant_type = 'client_credentials'
    * form field client_id = 'admin-api'
    * form field client_secret = client_secret
    * method post
    Then status 200
    And print response.access_token
    * def admin_token = 'Bearer ' + response.access_token
# create student schema
    Given url registryUrl
    And path 'api/v1/Schema'
    And header Authorization = admin_token
    And request read('StudentSchemaRequest.json')
    When method post
    Then status 200
    And response.params.status == "SUCCESSFUL"
  # invite entity for student
    Given url registryUrl
    And path 'api/v1/Student/invite'
    * def studentRequest = read('StudentRequest.json')
    And request studentRequest
    When method post
    Then status 200
  #  get student token
    * url authUrl
    * path 'auth/realms/sunbird-rc/protocol/openid-connect/token'
    * header Content-Type = 'application/x-www-form-urlencoded; charset=utf-8'
    * header Host = 'keycloak:8080'
    * form field grant_type = 'password'
    * form field client_id = 'registry-frontend'
    * form field username = studentRequest.contact
    * form field password = 'abcd@123'
    * method post
    Then status 200
    And print response.access_token
    * def student_token = 'Bearer ' + response.access_token
    * sleep(3000)
  # get student info
    Given url registryUrl
    And path 'api/v1/Student'
    And header Authorization = student_token
    When method get
    Then status 200
    And response[0].osid.length > 0
    * def studentOsid = response[0].osid
  # update student info
    Given url registryUrl
    And path 'api/v1/Student/' + studentOsid
    And header Authorization = student_token
    * set studentRequest.name = "xyz"
    And request studentRequest
    When method put
    Then status 200
    And response.params.status == "SUCCESSFUL"
  # verify student info after update
    Given url registryUrl
    And path 'api/v1/Student'
    And header Authorization = student_token
    When method get
    Then status 200
    And response[0].name == "xyz"
  # get student
    Given url registryUrl
    And path 'api/v1/Student/search'
    And request {"filters":{}}
    When method post
    Then status 200
    * print response
    And response.length == 1
    And match response[0].contact == '#notpresent'
    And match response[0].favoriteSubject == '#notpresent'
  # delete student info
    Given url registryUrl
    And path 'api/v1/Student/' + studentOsid
    And header Authorization = student_token
    When method delete
    Then status 200
    And response.params.status == "SUCCESSFUL"
  # verify student info after delete
    Given url registryUrl
    And path 'api/v1/Student'
    And header Authorization = student_token
    When method get
    Then status 404

  @env=async
  Scenario: Create a teacher schema and create teacher entity asynchronously
  #    get admin token
    * url authUrl
    * path 'auth/realms/sunbird-rc/protocol/openid-connect/token'
    * header Content-Type = 'application/x-www-form-urlencoded; charset=utf-8'
    * header Host = 'keycloak:8080'
    * form field grant_type = 'client_credentials'
    * form field client_id = 'admin-api'
    * form field client_secret = client_secret
    * method post
    Then status 200
    And print response.access_token
    * def admin_token = 'Bearer ' + response.access_token
# create teacher schema
    Given url registryUrl
    And path 'api/v1/Schema'
    And header Authorization = admin_token
    And request read('TeacherSchemaRequest.json')
    When method post
    Then status 200
    And response.params.status == "SUCCESSFUL"
  # create entity for teacher
    Given url registryUrl
    And path 'api/v1/Teacher?mode=async'
    * def teacherRequest = read('TeacherRequest.json')
    And request teacherRequest
    When method post
    Then status 200
    And response.result.Teacher.transactionId.length > 0
    * sleep(3000)
  # get teacher info
    Given url registryUrl
    And path 'api/v1/Teacher/search'
    And request {"filters":{}}
    When method post
    Then status 200
    * print response
    And response.length == 1
    And response[0].contact == '#notpresent'

  Scenario: Create Board and invite institutes
    #    get admin token
    * url authUrl
    * path 'auth/realms/sunbird-rc/protocol/openid-connect/token'
    * header Content-Type = 'application/x-www-form-urlencoded; charset=utf-8'
    * header Host = 'keycloak:8080'
    * form field grant_type = 'client_credentials'
    * form field client_id = 'admin-api'
    * form field client_secret = 'a52c5f4a-89fd-40b9-aea2-3f711f14c889'
    * method post
    * def sample = read('inviteFlow.json')
    Then status 200
    And print response.access_token
    * def admin_token = 'Bearer ' + response.access_token
  # create board schema
    Given url registryUrl
    And path 'api/v1/Schema'
    And header Authorization = admin_token
    And request sample.boardSchema
    When method post
    Then status 200
    And response.params.status == "SUCCESSFUL"
  # create institute schema
    Given url registryUrl
    And path 'api/v1/Schema'
    And header Authorization = admin_token
    And request sample.instituteSchema
    When method post
    Then status 200
    And response.params.status == "SUCCESSFUL"
   # invite institute without token should fail
    Given url registryUrl
    And path 'api/v1/Institute/invite'
    And request sample.instituteRequest
    When method post
    Then status 401
  # invite board
    Given url registryUrl
    And path 'api/v1/Board/invite'
    And request sample.boardInviteRequest
    When method post
    Then status 200
  #  get board token
    * url authUrl
    * path 'auth/realms/sunbird-rc/protocol/openid-connect/token'
    * header Content-Type = 'application/x-www-form-urlencoded; charset=utf-8'
    * header Host = 'keycloak:8080'
    * form field grant_type = 'password'
    * form field client_id = 'registry-frontend'
    * form field username = sample.boardInviteRequest.mobile
    * form field password = 'abcd@123'
    * method post
    Then status 200
    And print response.access_token
    * def board_token = 'Bearer ' + response.access_token
    * sleep(3000)
  # get board info
    Given url registryUrl
    And path 'api/v1/Board'
    And header Authorization = board_token
    When method get
    Then status 200
    And response[0].osid.length > 0

  # invite institute with token
    Given url registryUrl
    And path 'api/v1/Institute/invite'
    And request sample.instituteRequest
    And header Authorization = board_token
    When method post
    Then status 200
    #  get institute token
    * url authUrl
    * path 'auth/realms/sunbird-rc/protocol/openid-connect/token'
    * header Content-Type = 'application/x-www-form-urlencoded; charset=utf-8'
    * header Host = 'keycloak:8080'
    * form field grant_type = 'password'
    * form field client_id = 'registry-frontend'
    * form field username = sample.instituteRequest.mobile
    * form field password = 'abcd@123'
    * method post
    Then status 200
    And print response.access_token
    * def institute_token = 'Bearer ' + response.access_token
    * sleep(3000)
  # get institute info
    Given url registryUrl
    And path 'api/v1/Institute'
    And header Authorization = institute_token
    When method get
    Then status 200
    And response[0].osid.length > 0
    * def instituteOsid = response[0].osid
    * def address = response[0].address

  # update property of institute
    Given url registryUrl
    And path 'api/v1/Institute/'+instituteOsid+'/address/'+address[0].osid
    And address[0].phoneNo = ["444"]
    And request address[0]
    And header Authorization = institute_token
    When method put
    Then status 200
    * print response
  # check if array updated institute info
    Given url registryUrl
    And path 'api/v1/Institute'
    And header Authorization = institute_token
    When method get
    Then status 200
    And assert response[0].address[0].phoneNo.length == 1
    And assert response[0].address[0].phoneNo[0] == "444"