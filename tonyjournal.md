###### 2/26/16
I'm pretty sure accounts are finished now. When we tested it there
was an issue because the user ID field still existed, so Colin
removed it. Apparently removing fields is a challenge of the highest
order when using db-migrate. Other than that, it is now possible to
sign up for an account. I don't like that we are using username instead
of a numeric ID for uniquely identifying users, but I couldn't think
of a good reason not to so we're doing it. We are now trying to figure
out when/how the client and server will communicate. For example, when
an event happens like units leaving a node, the client tells the server
that it is moving those units. But does it tell the server how many
units leave that particular kind of node, or does the server figure it
out? And does the server tell all of the clients how long the units will
take to arrive? We want to make sure that the client and server do not
get out of sync, because that would ruin the match. I think we should
have the server tell all the clients every time an event begins or ends,
like when units are sent from a node as well as when they arrive at a new
one. That way, if one of the clients loses track of where some units are
while traveling between nodes, it can just catch them up when they
arrive. It might look a little bit weird if they just jump to the correct
position but that is unavoidable.

###### 2/19/16
This week I worked on making a sign-up page. The page itself right
now is just a minimal HTML form, because I don't really care about
that part very much. I had to learn how to access POST variables
with Node and ended up using a script from StackOverflow that
nicely puts them in an array for you to use. I also learned how to
query the PostrgreSQL database and began the slow process of
remembering how to use SQL. The page that runs the actual
sign-up process checks if the user exists and if they do, does
nothing. If they don't exist, it will create their entry in the database
(after I remember how to do that). We originally were going to
assign every user a unique numerical ID but instead decided to
make usernames unique for that purpose instead. Our vaguely
defined Deliverable 1 goal of a server is technically already met,
but what it will probably end up meaning is a server that can send
and receive messages from the client and discard them, as well as
managing accounts that mean nothing.

###### 2/12/16
This week I learned how to actually display a webpage with Node.
It seems surprisingly easy. Node handles most of the stuff and just
gives you variables for stuff like the page the user requested.
You can just print out whatever easily and that's it. I expected
to have to learn a bunch of stuff about HTTP but I didn't. We also
figured out how to use PostgreSQL with Node to easily access the
database that Heroku allows us to use. It was also much easier than
when I tried to create a website a few years ago. I set up the a tool
called migrate-db (or maybe it was db-migrate) that allows me to
set up the database on my computer. You create a simple Javascript
file with easily readable instructions on how to recreate the database
and it sets up the tables. It doesn't actually copy the data however,
but that shouldn't be an issue because we will mostly be working with
a few test values for now. Our next step is to create a signup page
where users can create an account to be used for playing the game. We
decided to make an account required to play the game because it would
be easier for us.

###### 2/5/16
This week we mostly ironed out how we are going to do the game.
Previously we were thinking of writing the server in Java for
consistency. We decided to use Node.js instead, because it seems
like it will be relatively easy. The client and server will
communicate with WebSockets. We figured out all the different
messages that will need to be sent and put them on the wiki
for our repository. These are for when someone sends units
from a node to attack, when a unit dies, and when the
battlefield loads and the game starts. We aren't sure exactly
how we are going to send the information for the battlefield
setup, because we don't know all the unique properties we want
nodes to have yet. Another question that came up was how the
attributes of nodes are determined; will it be at random or
pick from a set of options? We don't want to accidentally
create a node that starts out under one person's control and
is much more powerful than enemy nodes. I was also able to
convince Colin that the number of units sent from a node at
once should depend on the node they come from, and add a
limit to how often units can be sent from a node.
