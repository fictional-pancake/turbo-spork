###### 5/13/16
I finished writing the freeze spell. It prevents unit generation, death,
movement, and transferring ownership of the node. When we add other spells
we'll also have to make sure that frozen nodes aren't targetable. I haven't
committed the code to master yet because we don't actually have a way to do
spells, which is what I'm going to do next. I'm not sure how we're going to
do the energy node, because it won't really be like a normal node. It doesn't
make sense to select it, own it, or cast spells on it. However, if we make
it into something besides a node, we'll have to add some new stuff to support
it. Overall I think it will be easier to make it into a node with some weird
properties, and just automatically kill units that are sent to it. I don't
like the idea of having to check what node someone is operating on every time
they try to move units or anything, but there doesn't seem to be a great
alternative.

###### 5/6/16
This week we are starting to implement spells. We came up with a couple ideas
for spells we could potentially implement, including a "turbo spork" that
kills random units, which would be pretty hard to balance. One we are
actually implementing is a spell to freeze a node temporarily and prevent
anything from happening on that node while it is frozen. Another would
temporarily speed up the unit production of a friendly node. I am working on
the freezing a node one right now. I can't actually do the whole thing because
we haven't written support for spells in the first place yet. We are thinking
that there will be some spot (probably a node) where you can send units to be
sacrificed, and each unit that dies gives you 1 energy. Energy would be used
to cast spells, instead of some other method of limiting casts like cooldowns.
This node will probably be either in the middle of the map or the bottom center
so that no player is closer to it than the other. Still, on unfair maps, it
might be easier for one player to send units than the other, which could be a
slight problem.

###### 4/29/16
We've finished simplifying win detection. It now looks through every UnitGroup
and every node and if they both belong entirely to one player, that player
wins. It turns out that just looking at UnitGroups doesn't work because the
server only counts moving units as being UnitGroups. Chat is finished now.
When I first wrote it a long message would just cause the chat panel to
expand and cover the game panel, so we changed it to wrap the text instead.
The next thing I'm working on is a delay after the start game button is
pressed so the person who starts the game doesn't get an advantage. Some sort
of countdown would be ideal. I tried just running some wait function in the
client's startGame method, but awt had some error that I didn't understand.
I added a start delay to GameConstants that is sent to the server with
the gamestart command and the server waits that long before sending data back
to the client. It caused a NullPointerException in the method that uses all
the game data sent back from the server, so I assume it expected data when
none had been sent yet. I haven't figured out how to fix this yet. After I
finish that I want to work on spells, which should be weird and difficult to
implement.

###### 4/22/16
We implemented most of the things I talked about in the last journal entry.
Nodes still don't have unique properties, but I don't really think that's a
priority. I am currently working on adding chat. The chat box will be
displayed on the side panel next to the list of users. It will also be a
JList, and messages will be colored by owner. I learned more about how laying
things out works in order to put the chat box there and move other things
around. Another thing I want to change soon is simplifying the code that checks
for a win. Right now it is very complicated and also intertwined with code that
does other things every game tick. I plan to change it so it looks at every
UnitGroup in the game and checks who owns it. If they are all owned by one
player, that player wins the game. Currently win conditions are inconsistent
between players. I want to add spells to the game soon because I think they
will make it much more interesting, and are probably more important than
unique node properties.

###### 4/8/16
This week the game became playable, although it isn't really fun and kind
of sucks. It is now possible to move units between nodes as well as attack.
I think we need to make it so that if you attack an enemy node, you can call
your units off in the middle of the attack. Right now the game is too easily
decided by one huge battle in which one side loses all of their units. There
may also be some issues with the way nodes are layed out, because many
configurations favor one side over the other. We might end up having it
randomly choose from 10 or so configurations we've made that are balanced.
We'll also need to start giving nodes unique characteristics to make the game
more interesting. I'm not sure how much I care about improving graphics, but
if we decide to we might want to give things some sort of texture and make
units look better or move around the node they are at.

###### 4/1/16
I learned that it is possible to draw graphics using a component called a
JPanel, which is what are using for the graphics in our game. Right now
all we have for graphics are the nodes in the game. Even this relatively
simple task of drawing a bunch of circles has been fairly complicated. You
have to specify coordinates of where everything is in the JPanel and actually
add graphics using the Graphics class, which I'm still not sure of the function
of. Apparently when setting up the layout of a window you have to pick from a
set of options like BorderLayout or GridLayout. All of them seem to have
arbitrary restrictions and by comparison make HTML and CSS look efficient.
The game window uses BorderLayout, which only allows a window to have five
components, because it doesn't need that many different components. I am
currently in the middle of writing code to generate the units around their
nodes. We decided to make units stationary when they are sitting at a node
because it would look weird if they jumped around randomly and it would be
hard to make sure we didn't accidentally move them too much at once. Their
positions are generated by a Random object with the seed being the ID of the
node they belong to, to avoid two nodes in one game having the same unit
positions.

###### 3/25/16
Spring break

###### 3/18/16
The server is actually becoming a cohesive program. The game itself isn't
actually that complicated, so there isn't that much that still needs to be
added. If we decided to add more special traits to nodes or something like
spells that can be cast by players, that would make it more complex, but
that is definitely not a priority. One thing that still needs to be
implemented is fighting for nodes. If only one player's units are on a
node, they own that node, but it doesn't work properly if multiple
players have units on the node. We will want to make it so that the units
fight and whichever force is bigger wins. I have no idea how we are going
to make the graphics for the game. There's no way we are going to have
graphics for the client in time for the second deliverable. We are
probably only going to have part of the backend for it.

###### 3/11/16
It seems like Swing is just supposed to be used for stuff like menus. I don't
know how it is supposed to apply to moving graphics as is required for a game,
or if we are even going to use it for the game. It seems to just have
components like text boxes and stuff. The closest thing I've found to what
we need by looking through some documentation is the ability to show an icon.
Still I think that's pretty far from adequate for a game. I have no experience
with creating a GUI in any language so I don't really know, but I thought
for a game you are supposed to just figure out what's supposed to be on the
screen in the next frame and draw it every few milliseconds. Maybe there's
a framework for doing that that I don't know about. I've updated our second
deliverable to be sufficiently vague. It is now "a client", because we are
unlikely to have real graphics or much of a playable game.

###### 3/4/16
This week I didn't do that much, but Colin started work on the client.
I don't really understand how Java GUIs work. Apparently we are using
two tools called Swing and JFrame. From my understanding, JFrame
creates and handles windows and Swing puts content in the windows.
It seems like a lot of work to do simple things like create the
username and password fields. I suggested using some sort of thing
that allows us to write the GUI in HTML and have Java use it (which
I'm sure exists) but Colin said that would be much more difficult.
I don't know how we are going to do the graphics for the actual game,
because that will probably be much more complicated than creating text
input fields. There were some weird issues when we tried to have
multiple things listening for server input. With the library we are
using, it seems like only one class receives data from the server. To
get around this, we created a class that just calls the respective
functions (onData() and such) in all of the classes we want listening
for data. We have an ArrayList of everything that needs to listen in
that class.

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
