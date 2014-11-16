#wix-hive-scala
==============

Scala client for the Wix Hive API


## Prerequisites
- **Read about** [developing a third party app for the Wix platform](http://dev.wix.com/docs/display/DRAF/Third+Party+Apps+-+Introduction).
- **Register your app** [here](http://dev.wix.com/docs/display/DRAF/Dev+Center+Registration+Guide) to **obtain** your **APP_KEY** and **APP_SECRET**

## Installation

TODO: Maven /+ SBT
``` maven
<dependency>
    <groupId>com.wixpress</groupId>
    <artifactId>hive-api-scala</artifactId>
    <version>1.1.0-SNAPSHOT</version>
</dependency>
```

## Quick Start


### Configuration
The entry point to the Wix Hive API is the `HiveClient` class. You can initialize the class using a configuration file or by passing it the configuration values.

To use the configuration file ('reference.conf') just don't pass any parameters to the HiveClient() apply method.

####The configuration file looks like:
``` scala
hive-client {
  credentials {
    appId = "your app-id here"
    appSecret = "your secret-key here"
  }
  baseUrl = "https://openapi.wix.com"
}
```

You can override any of the parameters by passing it to the constractor.

1. The `hive-client.credentials.appSecret` and `hive-client.credentials.appId` are obtained by registering an app as it is outlined [here](http://dev.wix.com/docs/display/DRAF/Dev+Center+Registration+Guide)
2. The `instance` is obtained by decoding the signed app instance. Learn more about this  [here](http://dev.wix.com/docs/display/DRAF/Using+the+Signed+App+Instance)


Sample code to retrive contact by ID

``` scala
  val client = new HiveClient()
  client.execute(instance, GetContactById(contactId))
```
As a parameter you can use any class inherits from `HiveBaseCommand`

Thre's an alternative way which is easier if you need to execute multiple commands on the same instance. You can use the convenience method 'executeForInstance'
``` scala
  val executor = client.executeForInstance(instance)
  executor(GetContactById(contactId))
```

### Hive Errors
#### Response Errors
``` scala
    400 -> "Bad Request",
    403 -> "Forbidden",
    404 -> "NotFound",
    408 -> "Request timeout",
    429 -> "Too many requests",
    500 -> "Internal server error",
    502 -> "Bad gateway",
    503 -> "Service unavailable",
    504 -> "Gateway timeout"
```

### Contacts API

#### Concurrency Control
The contacts add and update methods have a concurrency control mechanism associated with them. The mechanism is based on the ``modifiedAt`` request parameter. This parameter needs to have the same value as the underlying contact that is being updated.
For example: let us assume we have a contact with ``id=1`` and ``modifiedAt=2014-10-01T14:43:48.560+03:00`` and we want to update the email field. What we would need to do is execute the following method:
``` scala
    TODO: Scala code
   new_email = Hive::Email.new
   new_email.tag = 'work_new'
   new_email.email = 'alex_new@example.com'
   new_email.emailStatus = 'optOut'

   client.add_contact_email('1', new_email, '2014-10-01T14:43:48.560+03:00')
```
So lets think about the concurrency now. Let assume we have two update email requests that come in the same time and they get processed sequentially.
First one would get processed and update the contact email and in the same time the contacts’ ``modifiedAt`` will change.
Second request gets processed but it will fail with a concurrency validation error because it is trying to perform an update operation on a old version of the contact object.
And the system knows that by comparing the two ``modifiedAt`` parameters (one from the DB and the one provided).

#### client.new_contact

**Example:**
``` ruby
contact = Hive::Contact.new
   contact.name.first = 'E2E'
   contact.name.last = 'Cool'
   contact.company.name = 'Wix'
   contact.company.role = 'CEO'
   contact.add_email(email: 'alext@wix.com', tag: 'work')
   contact.add_phone(phone: '123456789', tag: 'work')
   contact.add_address(tag: 'home', address: '28208 N Inca St.', neighborhood: 'LODO', city: 'Denver', region: 'CO', country: 'US', postalCode: '80202')
   contact.add_date(date: Time.now.iso8601(3), tag: 'E2E')
   contact.add_url(url: 'wix.com', tag: 'site')
   # PENDING
   # contact.add_note(content: 'alex', modifiedAt: '2014-08-05T13:59:37.873Z')
   # contact.add_custom(field: 'custom1', value: 'custom')
   client.new_contact(contact)
```

#### client.contact

**Example:**
``` ruby
client.contact(CONTACT_ID)
```

#### client.update_contact (PENDING)

**Example:**
``` ruby
   contact.add_email(email: 'wow@wix.com', tag: 'wow')
   contact.add_address(tag: 'home2', address: '1625 Larimer', neighborhood: 'LODO', city: 'Denver', region: 'CO', country: 'US', postalCode: '80202')
   contact.add_date(date: Time.now.iso8601(3), tag: 'E2E UPDATE')
   contact.add_url(url: 'wix.com', tag: 'site')

   # PENDING
   client.update_contact(CONTACT_ID, contact, MODIFIED_AT)
```

#### client.contacts_tags (PENDING)

**Example:**
``` ruby
client.contacts_tags
```

#### client.contacts_subscribers (PENDING)

**Example:**
``` ruby
client.contacts_subscribers
```

#### client.update_contact_name

**Example:**
``` ruby
client.update_contact_name(CONTACT_ID, Hive::Name.new(first: 'New_Name'), MODIFIED_AT)
```

#### client.update_contact_company

**Example:**
``` ruby
company = Hive::Company.new
   company.name = 'New_Company'

   client.update_contact_company(CONTACT_ID, company, MODIFIED_AT)
```

#### client.update_contact_picture

**Example:**
``` ruby
client.update_contact_picture(CONTACT_ID, 'wix.com/example.jpg', MODIFIED_AT)
```

#### client.update_contact_address

**Example:**
``` ruby
updated_address = Hive::Address.new
   updated_address.tag = 'work'
   updated_address.address = '1625 Larimer St.'

   client.update_contact_address(CONTACT_ID, ADDRESS_ID, updated_address, MODIFIED_AT)
```

#### client.update_contact_email

**Example:**
``` ruby
updated_email = Hive::Email.new
   updated_email.tag = 'work'
   updated_email.email = 'alex@example.com'
   updated_email.emailStatus = 'optOut'

   client.update_contact_email(CONTACT_ID, EMAIL_ID, updated_email, MODIFIED_AT)
```

#### client.update_contact_phone

**Example:**
``` ruby
updated_phone = Hive::Phone.new
   updated_phone.tag = 'work'
   updated_phone.phone = '18006666'

   client.update_contact_phone(CONTACT_ID, PHONE_ID, updated_phone, MODIFIED_AT)
```

#### client.update_contact_date

**Example:**
``` ruby
date = Hive::Date.new
   date.date = Time.now.iso8601(3)
   date.tag = 'update'

   client.update_contact_date(CONTACT_ID, DATE_ID, date, MODIFIED_AT)
```

#### client.update_contact_note (PENDING)

**Example:**
``` ruby
note = Hive::Note.new
   note.content = 'Note'
   note.modifiedAt = Time.now.iso8601(3)

   client.update_contact_phone(CONTACT_ID, NOTE_ID, note, MODIFIED_AT)
```

#### client.update_contact_custom (PENDING)

**Example:**
``` ruby
custom = Hive::Custom.new
   custom.field = 'custom_update'
   custom.value = 'custom_value'

   client.update_contact_phone(CONTACT_ID, CUSTOM_ID, custom, MODIFIED_AT)
```

#### client.add_contact_address

**Example:**
``` ruby
new_address = Hive::Address.new
   new_address.tag = 'work'
   new_address.address = '1625 Larimer St.'

   client.add_contact_address(CONTACT_ID, new_address, MODIFIED_AT)
```

#### client.add_contact_email

**Example:**
``` ruby
new_email = Hive::Email.new
   new_email.tag = 'work_new'
   new_email.email = 'alex_new@example.com'
   new_email.emailStatus = 'optOut'

   client.add_contact_email(CONTACT_ID, new_email, MODIFIED_AT)
```

#### client.add_contact_phone

**Example:**
``` ruby
new_phone = Hive::Phone.new
   new_phone.tag = 'work_new'
   new_phone.phone = '18006666'

   client.add_contact_phone(CONTACT_ID, new_phone, MODIFIED_AT)
```

#### client.add_contact_note
**Example:**
``` ruby
note = Hive::Note.new
   note.content = 'Note'

   client.add_contact_note(CONTACT_ID, note, MODIFIED_AT)
```

#### client.add_contact_custom

**Example:**
``` ruby
custom = Hive::Custom.new
   custom.field = 'custom_update'
   custom.value = 'custom_value'

   client.add_contact_custom(CONTACT_ID, custom, MODIFIED_AT)
```

#### client.add_contact_tags (PENDING)

**Example:**
``` ruby
tags = ['tag1/tag', 'tag2/tag']

   client.add_contact_tags(CONTACT_ID, tags, MODIFIED_AT)
```

#### client.add_contact_activity

**Example:**
``` ruby
FACTORY = Hive::Activities
activity = Hive::Activity.new(
       type: FACTORY::MUSIC_ALBUM_FAN.type,
       locationUrl: 'http://www.wix.com',
       details: { summary: 'test', additionalInfoUrl: 'http://www.wix.com' },
       info: { album: { name: 'Wix', id: '1234' } })

   client.add_contact_activity(CONTACT_ID, activity)
```

#### client.contact_activities

**Example:**
``` ruby
client.contact_activities(CONTACT_ID)
```

#### client.contacts

**Examples:**
``` ruby
client.contacts
client.contacts( pageSize: 50 )
client.contacts( tag: 'contacts_server/new' )
client.contacts( email: 'alex@example.com' )
client.contacts( phone: '123456789' )
client.contacts( firstName: 'E2E' )
client.contacts( lastName:'Cool' )
```

#### client.upsert_contact

**Examples:**
``` ruby
client.upsert_contact( phone: '123456789' )
client.upsert_contact( email: 'alex@example.com' )
client.upsert_contact( phone: '123456789', email: 'alex@example.com' )
```

### Activities API
**Note**: Activity info is created via a factory: 'FACTORY = Hive::Activities'

#### client.new_activity

**Example:**
   ``` ruby
   Hive::Activity.new(
           type: FACTORY::MUSIC_ALBUM_FAN.type,
           locationUrl: 'http://www.wix.com',
           details: { summary: 'test', additionalInfoUrl: 'http://www.wix.com' },
           info: { album: { name: 'Wix', id: '1234' } })

   client.new_activity(SESSION_ID, base_activity)
   ```

#### client.activity

**Example:**

   ``` ruby
   client.activity(ACTIVITY_ID)
   ```

#### client.activities

**Examples:**
   ``` ruby
   client.activities
   client.activities(activityTypes: Hive::Activities::MUSIC_ALBUM_FAN.type)
   client.activities(from: Time.now.iso8601(3), until: Time.now.iso8601(3))
   ```

### Insights API

#### client.activities_summary

**Example:**
   ``` ruby
   client.activities_summary
   ```

#### client.contact_activities_summary

**Example:**
   ``` ruby
   client.contact_activities_summary(CONTACT_ID)
   ```
## Contributing

**Everyone** is encouraged to help **improve** this library. Some of the ways you can contribute include:

1. Use alpha, beta, and pre-release versions.
2. Report bugs.
3. Suggest new features.
4. Write or edit documentation.
5. Write specifications.
6. Write code (**no patch is too small**: fix typos, clean up inconsistent whitespace).
7. Refactor code.
8. Fix [issues](https://github.com/wix/wix-hive-scala/issues).
9. Submit an Issue

### Submitting an Issue

We use the GitHub issue tracker to track bugs and features. Before submitting a bug report or feature request, check to make sure it hasn't already been submitted. When submitting a bug report, please include a Gist that includes a stack trace and any details that may be necessary to reproduce the bug, including your Scala version, and operating system. Ideally, a bug report should include a pull request with failing specs.

### Submitting a Pull Request

1. Fork it ( https://github.com/[my-github-username]/wix-hive-scala/fork )
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Add specs for your unimplemented feature or bug fix. (**Note:** When developing a new API a `e2e` test is mandatory.)
4. Run `mnv clean install`. If your specs pass, return to step 3.
5. Implement your feature or bug fix.
6. Run `mvn clean install`. If your specs fail, return to step 5.
7. Commit your changes (`git commit -am 'Add some feature'`)
8. Push to the branch (`git push origin my-new-feature`)
9. Create a new [Pull Request](http://help.github.com/send-pull-requests/)
