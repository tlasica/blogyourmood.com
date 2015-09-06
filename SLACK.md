#How to integrate blogyourmood with #slack

It is very easy to let your teammates record their mood using using #slack commands (see more at https://api.slack.com/slash-commands)

Because [blogyourmood.com](http://blogyourmood.com) does not require any additional authentication the only thing you have to know is the URL of your mood blog.

Let's configure simple **/happy** command:

1. Go to your slack team Home page and choose "Integration".
2. Choose "Or, make your own!" option to configure slash commands
3. Choose "Slash commands" - the very bottom on the page

Now put your command name: eg. **/happy** and "Add..." it.

The correct URL for the POST request is

    http://www.blogyourmood.com/blog/YOUR-BLOG-UUID/happy

Replace happy with any of {happy, angry, normal, sad}.

My favorite command is */wtf*


