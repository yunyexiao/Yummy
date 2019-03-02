# Some Notes On This Project

## 0 Eager fetch in model.Restaurant and model.RestaurantDraft
It says that nullable one-to-one relationship can only be eager-fetched even 
though the `fetch = FetchType.LAZY` is specified.

Of course, there are some solutions like compile-time instrument(?), make it a
one-to-many relationship, or make it non-nullable by `optional = false`. None 
of them can satisfy me though.

## 1 Restaurant sign up hell
According to current sign up process, if plenty (>= 62^7) of sign up requests 
come, there would be no available rid. Also, sessions would extremely increase
and break up the server.

Seems we have to limit restaurant sign up requests.
