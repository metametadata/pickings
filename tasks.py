from invoke import task, run
import os


################################################### COMMON TASKS
@task
def clean():
    """ Cleans build artifacts. """
    lein("clean")

################################################### DEVELOPMENT TASKS
@task
def repl():
    """ [For development] Starts REPL. REPL allows to perform common tasks (including tests running) without lein startup overhead. """
    lein("repl")

################################################### PRODUCTION TASKS
@task()
def uberjar():
    """ Builds app uberjar. To run the app: java -jar <path to compiled standalone jar>"""
    lein("uberjar")

@task()
def mac():
    """ Packages uberjar as a Mac OS app."""
    packager = os.path.join(run("/usr/libexec/java_home").stdout.rstrip(), "bin/javapackager")
    run('{0} -deploy -native image'
        ' -outdir out -outfile pickings.app'
        ' -srcfiles target/pickings-0.4.0-standalone.jar -appclass pickings.main'
        ' -name "Pickings" -title "Pickings"'
        ' -Bruntime= -Bicon=resources/icon.icns'.format(packager),
        echo=True)

################################################### HELPERS
def lein(args, rlwrap=False):
    run("{0}lein {1}".format("rlwrap " if rlwrap else "",
                             args),
        echo=True,

        # without pty 'lein repl' glitches
        pty=True)