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
