### **What is Agile based development?**

And why should you care? Agile software development is a set of principles for
software development in which the requirements and solutions are constantly
evolving throughout all stages of the project. It’s a powerful tool when used
correctly and places emphasis on delivering working software over heavily
regulated documentation and micromanaging.

With that in mind, this web app was designed to help plan an agile based project
and aid students implement the methodologies that they will be learning
throughout their degree, and  
put these into practice for their third and fourth year projects.

So, let’s start with some jargon busting for some of the features of this app.


**Product backlog:**

This is the list of tasks that are to be completed to bring about the next
iteration or prototype of the product to be made.


**Epic:**  
A large task that can be further broken up into subtasks. They are often
compared to stories, which details the requirements of a project. Example,
“Shopping cart page” can be broken up into subtasks like, “create ui”
“add/remove items from cart” and so on so forth.


**Task:**  
A task is the smallest unit of a sprint; this is an objective belonging to the
product backlog that must be completed to advance project development.
Information concerning a task includes the creator of the task, the people
assigned to it, the estimation of how long it will take, the current logged
hours on it, and then the priority of the task.


**Sprint:**

A sprint can be thought of as a unit of time decided by the team, whether a
week, two weeks or a month. Tasks are assigned to a particular sprint from the
product backlog and the team endeavours to complete those tasks by the end of
the sprint, otherwise it’s carried forward on to a new sprint. The best way of
thinking of a sprint is a large board with 3 columns, “To be done”, “In
Progress” and “Completed”, tasks can be thought of as post-it notes with the
tasks relevant details.

Team members will assign themselves to tasks and take ownership of them, moving
them from the “To be done” column to the “In Progress” and then unsurprisingly
to the “Completed” one when they are finished. This allows all members of the
project to see the current progress of a sprint and know where they stand at any
given time. Generally, a sprint will be comprised of tasks chosen from the
backlog from the “Sprint master” but for this “shared workspace” app anyone can
assign tasks from the backlog.

So, with all that out of the way, let’s move on to how to use Agile Task
Tracker.

### **The Home page**

![](http://i.imgur.com/Za33d9E.png)

When you first land on the web app, this is the first page you’ll find, here you
can manage your “Organisations” which can be viewed as course modules, which in
turn will contain your projects/assignments. To add a new organisation, simply
click on create organisation and you will be prompted to enter an ID and name
for it. This will generate a new button named after your organisation. Refresh
organisations allows you to query for any organisations that were added by
people in your work group.

Clicking on these organisation buttons will bring you to the project page where
you can manage your current projects and add new ones.

###  **Project page**

![](http://i.imgur.com/3aOOlhJ.png)
From the organisation page, you’ll end up on the projects page, detailing any
projects associated with the organisation/module. Same as organisation, click
create project and you’ll be prompted to enter the project-id, name, the number
of man-hours that you and your team have available to dedicate to the project
from start to end and finally the start and end date of the project. In future
releases you will be able to track your progress with estimated man hours’ vs
actual hours per sprint.

Clicking the project button will then bring you to the project’s product backlog
where we can start creating tasks.

### **Product Backlog**

![](http://i.imgur.com/HUZBIhF.png)

This is where things start to get a little interesting. On the left side, you
have your product backlog, which allows you to create new tasks that are added
to the backlog itself, and on the right side the sprint-creation column, where
you can drag your newly created tasks and assign them to a new sprint.

So, let’s start by adding a new task to our backlog. Click “Create Task” and
give the task an id and a name. A brief description of the task is helpful but
not necessary, mark yourself as the creator. You don’t have to give an assignee
right now, that can be specified when a task is added to a sprint. You do
however, must give an estimated time to complete the task, in Agile this is a
practice and a good habit to get into. Don’t worry if your estimate is off, it
can be updated afterward.

By default, the task will be set to a low priority but you can select medium or
high, and this will be flagged on the task so team members can see what tasks
are more important than others at a glance. If a task is part of an epic you can
specify it also, but is not required. Once you are happy with the task info, you
can hit save. And you should have your very first task.

![](http://i.imgur.com/LEnKenK.png)

To review any of the info within the task just click the plus symbol to maximise
the tasklet. You also have the option to edit any of the info, or delete the
task.

Now that you’ve made a few tasks, it’s time to start a sprint, so click and drag
the header of any tasks you want assigned to the sprint to the sprint column.
Don’t worry if you drag the tasks over then stop there. The next time you come
back they will still be assigned to the sprint column, ready to add to a sprint.
Next you just enter a sprint ID, name it, and specify the start and end dates
optionally. Just click save and now your tasks are assigned.

If you wish to add more tasks to a current sprint you can just come back and add
more tasks to the sprint column and just hit save, no need to enter the details
again, and they will be added to the current sprint along with any of the
previous tasks

  
  


![](http://i.imgur.com/2WOHTRl.png)

### **Current Sprint Page**

![](http://i.imgur.com/A7DFN3C.png)

The sprint page is basically a glorified notice board. You will have the three
aforementioned columns, “To Do”, “In Progress” and “Completed”. Any tasks you
assigned to your sprint should now be shown in the “To do”. Here is where you
start to take responsibility for tasks.  
Choose a task, expand it out, and hit edit, fill in the name of the assignee,
and save it. Now you can move it to “In Progress” and it will be updated on the
database and anyone viewing the sprint can see you are currently working on
that.

It’s good practice to keep a log of hours spent working on a task, so you can
add hours to the task by editing it. And if you don’t complete by the estimated
time, give a new estimate. This is all a good practice in Agile development.
Once you have completed a task, move it to the “Completed” column to flag it as
done on the database.

![](http://i.imgur.com/Ki3mh8Z.png)

  
Another good practice is to keep to a sprint’s timeframe. When the deadline has
passed, leave the tasks in their current columns and just navigate back to the
backlog page. On the sprint creation column, click “End sprint”. This will
deactivate the current sprint and will pull any uncompleted tasks back into the
sprint column, so you don’t have to remake them, or pull them from your backlog
again. Now when you start a new sprint, you can assign new tasks along with any
uncompleted tasks from the previous one.

![](http://i.imgur.com/gerCR75.png)
