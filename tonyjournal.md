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
