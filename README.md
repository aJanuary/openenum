# open enum
Extend enums with unknown values.

## Description
Java enums provide a closed set of values. This can be useful when combined
with exhaustiveness checks to ensure you are handling every value.
However, sometimes you want to combine the benefit of exhaustiveness checks
with the possibility of having unknown, unforeseen values. This is where
open enums come in: they contain either an enum value or an unknown value.

## Example
```java
public enum AccountType {
  Standard,
  Business,
  Enterprise;

  public static OpenEnum<AccountType, String> fromName(String name) {
    for (AccountType accountType : AccountType.values()) {
      if (accountType.name().equals(name)) {
        return OpenEnum.fromEnum(accountType);
      }
    }
    return OpenEnum.fromUnknown(name);
  }
}

var response = accountAPI.getDetailsFor(accountName);
var accountType = AccountType.fromName(response.get("account-type"));
var priceLabel = accountType.map(e -> switch (e) {
  // When new account types are added to the enum, this will generate an error for the missing case.
  case Standard -> "$10";
  case Business -> "$50";
  case Enterprise -> "$150";
}).orElse(u -> {
  // If the API adds a new account type before we've added it to our enum, we'll still be able to
  // pass the value around and generate useful error messages.
  return "Sorry, we don't have a price for " + u + " accounts yet. Check back later.";
});
```

## Building
Run `./gradlew build`