# clj.clojureranker

- Rescore Solr scoring functions with clojure functions
- Connect to nRepl for fast development cycle
- Use the hole Clojre ecosystem while rescoring
- Build Solr plugins without repacking jars and restarting Solr all the time 


## Usage

You can write clojure functions to rescore the Solr results.

It rescores only the n-top results in the query, the top-parameter defines how many to rescore

It should be pretty fast to start going

- Checkout this project and run: lein uberjar
- Copy the uberjar in the target dir into solr classpath
    - If you put the jar in solr/lib add startup="lazy" to you requesthandler
    - I normally put the jar in the core-dir/lib (you will have to create this dir)
- If one use the solr-config below you will be up and running default rescorer, which rescores random 
- Create your own leningen project and add to solr classpath, this should contain the new rescore function or just keep working on this leiningen project
- Update the solr config with require and config to reach the new rescore function   

### Even simpler distribution

If you think creating a leningen project is overkill, you can also do use the "load-file" parameter which should point to an absolute file path.

The plugin will the a load-file on this file at startup.

### Solr configuration

```xml
  <searchComponent name="cselect" class="clojureranker.Rescorer">
     <lst name="defaults">
       <bool name="start-nrepl">true</bool>
       <str name="searchComponentName">cselect</str>     
       <str name="require">clojureranker.test</str>  
       <str name="function">clojureranker.test/rescore</str> 
       <int name="top">30</int>                      
     </lst>
  </searchComponent>
```

Then add this lines to your request handler to activate the component:

```xml
     <arr name="last-components">
       <str>cselect</str>
     </arr>
```

Note:

- You need to repeat the searchcomponent-name in the defaults config (like above)
- Start repl with the start-nrepl-param. Only one repl will be started pr. solr instance
- You can have different search-components if you need different rescore-functions on different cores

### Rescore function

Example on the look of a rescore function:

```clojure

(defn rescore [score_list]
  "this is only a test rescore function"
  (map (fn [doc]
         (let [old-score (first doc)
               lucene-id (second doc)
               solr-doc (nth doc 2)
               new-score (if (= (.get solr-doc "id") "055357342X") 1 (rand))
               ]
           [new-score lucene-id])
         ) score_list)
  )

```
The input to the rescore function is a list of lists like this

```clojure
 [[score lucene-id solr-doc] [score lucene-id solr-doc] [score lucene-id solr-doc] ...]
```

The return of the function must be a list of type

```clojure
 [[new-score lucene-id] [new-score lucene-id] [new-score lucene-id] ...]
```
To note:
- Sorting will be handles by the framework, you just provide the new score
- All solr fields are available with the get-function above
- In the example above I just random score all hits, except if the id is 055357342X. Then I score this to 1, so this should always be on the top.


## nRepl

Repl is started at 7888, connect with your favorite editor and recompile and test out on the fly.
There is no long restart, packing cycles, but when you require new packages in the project file you will have to rebuid and restart solr.

The repl should off course only be run in debug environments, as it is a loaded gun :)

## Speed

It is pretty fast and I cant hardly notice the difference between a normal solr query and a rescored one.

But if you do heavy stuff, like getting info through http-requests and/or heavy vector calculations response time will probably rise. 

## Contributions and feedback

is off course welcome. Just drop create a pull request and drop me a note.

## TellusR

My company, [Sannsyn](https://sannsyn.com), is working on a plugin called TellusR to do stuff like this in Solr:

- AB-testing directly in Solr
- Boosting, tuning based on ai
- Personalization based on semantic and/or click/purchase info
- Statistics to see how the search is used:
    - Most used terms
    - Trending stuff
    - Which stuff converts best to click/buys
    - Find which articles which never is shown in hit lists
    - Find articles which are shown, but does not convert
    - Number of zero-hits, how these trends and which terms these are
    - Avg hits pr day, distribution through time and so on
    - Response time
    - Request times
    - We use smart algorithms and anomaly detections to warn you about trouble
- Gui to synonyms, elevation and advanced boosting rules
- More features coming :)

We also adopt the plugin for larger customer if needed.

Parts of this will be open sources, stay tuned or if you are interested, just drop us a line to get some early info 

## Embedding and boostrapping the clojure interpreter

This line did cost me my last non-grey hair straw, but it made me available to embed and boostrap the clojure interpreter from Solr:


```clojure
    Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
```

I mention here specifically as I might save some work for some other.

Drop me a line if you have an alternative approach.

## Solr versions

This plugin is compiled against solr 8.4.1-core. Chances are good that it will work out of the box with newer/older versions as well.

But if you would like to be certain, just checkout and change the 8.4.1 in the project-file to your solr version and the run:

lein uberjar

The new jar to add to Solr will be in the target-dir

## Credits

This plugin is loosely based on info in [this article](https://tech.finn.no/2018/04/10/personalized-search/)

Thanks for for open sourcing! 


## License

Copyright © 2020 Petter Egesund and Sannsyn

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
