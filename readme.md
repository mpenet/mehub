# mehub

Personal github homepage loading data from github and twitter, and
possibly other services.

It uses ClojureScript, less, bootstrap and font awesome.

## Usage

Just clone it in yourname.github.com, open index.html and adjust the
settings in the last script tag. This should be self explanatory.

```javascript
mehub.core.init({
   modules: {
      github: {
        user: 'mpenet'
       , 'repo-selector': "#gh-repositories"
       , 'user-selector': "#gh-user"
       , 'activity-selector': "#gh-activity"
      }
      , twitter: {
          selector: '#twitter-activity'
          , user: 'mpenet'
          , 'max-tweets': 3
      }
    }
  })
```

### Adding modules

You need to create a module file in src-cljs/mehub/module/ , create a
new type (that extends the PModule proto), extend the load-module
multimethod with its instanciation and add it to the core.cljs file ns
header. This should be easy if you use one of the existing one as example.

### Design Customization

The less entry point is resources/less/index.less.
The bootstrap files are not meant to be modified, allowing easy upgrades.
The css folders just hold compiled files, they shouldn't be modified.

## License

Copyright (C) 2012 Max Penet

Distributed under the Eclipse Public License, the same as Clojure.
