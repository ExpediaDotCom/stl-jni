# stl-jni

Java wrapper around the Fortran STL implementation. Uses JNI (Java -> C -> Fortran).

## Building

    $ ./gradlew build

This builds the Fortran, C and Java code. Note that the build is platform-specific, and currently runs only on a Mac.
I don't have any current plans to create builds for other platforms (the plan is to implement a pure Java version of
the library).

## Uploading to Nexus

First, create a `gradle.properties` file (see `gradle.properties.sample`). You'll need to put your Nexus URL and AD
credentials in there.

Then

    $ ./gradlew uploadArchives

After that I typically remove my password so it's not sitting around in a properties file.

## Making the dynamic lib available to other apps

On a Mac, put the library here:

    /Library/Java/Extensions/libstl_driver.jnilib

## Running the samples

First, make sure you've installed the R [ggplot2](http://ggplot2.org/) package.

Then you can run any of the `run-xxx` scripts in the `samples` directory.

## Run STL from R

If you want to look at the sample data entirely from R, you can do that too. Here's an example. (Do this from the
`samples/data` directory.)

~~~ R
requests <- read.table("eqs-requestper10mins.dat", head=FALSE)[,1]
requests.ts <- ts(requests, frequency=144)
fit <- stl(requests.ts, "periodic")
plot(fit)

seasonal.ts <- fit$time.series[, "seasonal"]
trend.ts <- fit$time.series[, "trend"]
fit.ts <- seasonal.ts + trend.ts

library(ggplot2)
df <- data.frame(x=seq(1, 3024), y=requests.ts)
band <- data.frame(x=df$x, ymin=fit.ts-5000, ymax=fit.ts+5000)
all.data <- merge(df, band, by.x='x')
ggplot(all.data, aes(x=x, y=y)) +
  theme_bw() +
  geom_ribbon(aes(x=x, ymin=ymin, ymax=ymax), fill="gray", alpha=0.5) +
  geom_line(linetype="solid")
~~~

## Notes

The log transform fails if there are zeroes in the dataset, for obvious reasons.

## License

See LICENSE in this directory.
