-- Add Pubsec officers
INSERT INTO users (name, email, password) VALUES ('Jane Doe', 'janedoe@eridumail.com', 'SneakyRat69');
INSERT INTO users (name, email, password) VALUES ('Seth Lowell', 'sethlowell@eridumail.com', 'FuzzyCat42');
INSERT INTO users (name, email, password) VALUES ('Zhu Yuan', 'zhuyuan@eridumail.com', 'BakeryExpert20');
INSERT INTO users (name, email, password) VALUES ('Qingyi', 'qingyi@eridumail.com', 'JadeDroid15');
INSERT INTO users (name, email, password) VALUES ('Hoshimi Miyabi', 'hoshimimiyabi@eridumail.com', 'CaptainLongEars15');
INSERT INTO users (name, email, password) VALUES ('Tsukishiro Yanagi', 'tsukishiroyanagi@eridumail.com', 'PinkOni20');
-- Add Belobog Industries workers
INSERT INTO users (name, email, password) VALUES ('Koleda Belobog', 'koledabelobog@eridumail.com', 'RedHamster18');
INSERT INTO users (name, email, password) VALUES ('Grace Howard', 'gracehoward@eridumail.com', 'IronWitch21');
INSERT INTO users (name, email, password) VALUES ('Anton Ivanov', 'antonivanov@eridumail.com', 'DrillExpert23');
INSERT INTO users (name, email, password) VALUES ('Ben Bigger', 'benbigger@eridumail.com', 'TheManager30');

-- Add importants projects
INSERT INTO projects (name, description, start_date) VALUES ('Mini Factory', 'Mini factory project in the border of the New Eridu', '2010-02-01');
INSERT INTO projects (name, description, start_date) VALUES ('Mejila pubsec settlement', 'Install a new settlement in the area of Mejila', '2007-04-03');
INSERT INTO projects (name, description, start_date) VALUES ('Explore the lands of the mist', 'Explore the lands of the mist from the Mejila settlement', '2007-09-03');

-- Add Belobog to the mini factory project
INSERT INTO project_user (project_id, user_id, role) VALUES (1, 7, 0);
INSERT INTO project_user (project_id, user_id, role) VALUES (1, 8, 1);
INSERT INTO project_user (project_id, user_id, role) VALUES (1, 9, 1);
INSERT INTO project_user (project_id, user_id, role) VALUES (1, 10, 1);

-- Add Section 6 members to the Mejila settlement exploration
INSERT INTO project_user (project_id, user_id, role) VALUES (2, 5, 0);
INSERT INTO project_user (project_id, user_id, role) VALUES (2, 6, 0);

-- Add Pubsec as members to the Mejila settlement defense
INSERT INTO project_user (project_id, user_id, role) VALUES (2, 1, 1);
INSERT INTO project_user (project_id, user_id, role) VALUES (2, 2, 1);
INSERT INTO project_user (project_id, user_id, role) VALUES (2, 3, 1);
INSERT INTO project_user (project_id, user_id, role) VALUES (2, 4, 1);

-- Add Belobog as members ONLY to the Mejila settlement project
INSERT INTO project_user (project_id, user_id, role) VALUES (2, 7, 1);
INSERT INTO project_user (project_id, user_id, role) VALUES (2, 8, 1);
INSERT INTO project_user (project_id, user_id, role) VALUES (2, 9, 1);
INSERT INTO project_user (project_id, user_id, role) VALUES (2, 10, 1);

-- Add Section 6 members to the Lands of the mist exploration
INSERT INTO project_user (project_id, user_id, role) VALUES (3, 5, 1);
INSERT INTO project_user (project_id, user_id, role) VALUES (3, 6, 0);

-- Add Pubsec as observers only to the lands of the mist exploration
INSERT INTO project_user (project_id, user_id, role) VALUES (3, 1, 2);
INSERT INTO project_user (project_id, user_id, role) VALUES (3, 2, 2);
INSERT INTO project_user (project_id, user_id, role) VALUES (3, 3, 2);
INSERT INTO project_user (project_id, user_id, role) VALUES (3, 4, 2);

-- Add start of the construction to the project
INSERT INTO tasks (name, description, end_date, task_priority, task_status, project_id, user_id) VALUES ("Inspect the terrain", "Inspect the terrain in the abandoned sector 5 for the mini factory", "2010-02-07", 4, 3, 1, 7);
INSERT INTO tasks (name, description, end_date, task_priority, task_status, project_id, user_id) VALUES ("Repair the terrain", "Repair the holes in the terrain with the other workers", "2010-03-021", 4, 1, 1, 7);
INSERT INTO tasks (name, description, end_date, task_priority, task_status, project_id, user_id) VALUES ("Clean up the abandoned factories", "Clean up the factories from the old items and papers", "2010-02-21", 3, 2, 1, 10);
INSERT INTO tasks (name, description, end_date, task_priority, task_status, project_id, user_id) VALUES ("Demolish the concrete walls", "Demolish the concrete walls that got added as an extension to the terrain", "2010-03-14", 3, 2, 1, 10);
INSERT INTO tasks (name, description, end_date, task_priority, task_status, project_id, user_id) VALUES ("Remove the broken pipes", "Remove the rusty and broken metal pipes in order to replace them", "2010-02-21", 3, 2, 1, 9);
INSERT INTO tasks (name, description, end_date, task_priority, task_status, project_id, user_id) VALUES ("Install new pipes", "Install the new metal pipes in the factory", "2010-03-07", 3, 1, 1, 9);
INSERT INTO tasks (name, description, end_date, task_priority, task_status, project_id, user_id) VALUES ("Check the electricity", "Check if the wirings are still in correct shape, replace them otherwise", "2010-02-28", 3, 2, 1, 8);
INSERT INTO tasks (name, description, end_date, task_priority, task_status, project_id, user_id) VALUES ("Add new wirings", "Add new wirings in order to install modern computers with ethernet connection", "2010-03-21", 3, 1, 1, 8);

-- Add start of the Mejila project
INSERT INTO tasks (name, description, end_date, task_priority, task_status, project_id, user_id) VALUES ("Examine the terrain", "Make a report on the state of the terrain surrounding the area", "2010-04-14", 4, 2, 2, 7);
INSERT INTO tasks (name, description, end_date, task_priority, task_status, project_id, user_id) VALUES ("Clean up the building", "Clean up the building from the old items and papers", "2010-04-20", 2, 3, 2, 10);
INSERT INTO tasks (name, description, end_date, task_priority, task_status, project_id, user_id) VALUES ("Clear the rubble", "Clear the collapsed walls near the main exit", "2010-04-20", 3, 2, 2, 10);
INSERT INTO tasks (name, description, end_date, task_priority, task_status, project_id, user_id) VALUES ("Clear the old walls", "Clear the damaged walls in order to rebuild them", "2010-04-28", 3, 2, 2, 9);
INSERT INTO tasks (name, description, end_date, task_priority, task_status, project_id, user_id) VALUES ("Reconnect the electricity", "Reconnect the electricity to the central and and add wirings for computing services", "2010-04-28", 3, 2, 2, 8);
INSERT INTO tasks (name, description, end_date, task_priority, task_status, project_id, user_id) VALUES ("Area inspection", "Inspect the area for any suspicious materials, remove them with your team if there are any", "2010-04-10", 4, 3, 2, 1);
INSERT INTO tasks (name, description, end_date, task_priority, task_status, project_id, user_id) VALUES ("Area securisation", "Help secure the doors and windows  with reinforced doorframe and windowframe", "2010-04-28", 3, 1, 2, 2);
INSERT INTO tasks (name, description, end_date, task_priority, task_status, project_id, user_id) VALUES ("Install utilities", "Install all the utilities in the infirmary and the locals", "2010-04-14", 3, 1, 2, 3);
INSERT INTO tasks (name, description, end_date, task_priority, task_status, project_id, user_id) VALUES ("Install computers", "Install and setup the computers for the network", "2010-04-28", 3, 1, 2, 4);

INSERT INTO tasks (name, description, end_date, task_priority, task_status, project_id, user_id) VALUES ("Area scouting", "Scout the area surrounding the settlement for potential threats", "2010-04-10", 4, 0, 3, 5);
INSERT INTO tasks (name, description, end_date, task_priority, task_status, project_id, user_id) VALUES ("Camp installation", "Install the camp outside the settlement for nightwatch", "2010-04-10", 4, 0, 3, 6);

