# Computational Graph Viewer

## Description
The Computational Graph Viewer is a tool designed to visualize and interact with computational graphs. It is served by an HTTP server with servlets, allowing users to upload and view configurations of computational graphs dynamically.

## Table of Contents
1. [Installation](#installation)
2. [Run](#run)
3. [Usage](#usage)
4. [Configuration Files](#configuration-files)
5. [Features](#features)

## Installation

1. Clone the repository:
   
   git https://github.com/danihazan/Graph-Project-Advanced-Programming.git
   
![image](https://github.com/user-attachments/assets/b067c999-d148-476a-b731-176cfe5ede21)

## Run
### Running The Project

   1. Navigate to the project directory:

      cd Graph-Project-Advanced-Programming
      
      ![image](https://github.com/user-attachments/assets/5e370235-a531-45a6-94b0-32643377f37d)
   3. Run the Main script:
      
      #### When java is Not in Your Environment Variables
      
         Use the full path to the java executable:
      
         "C:\path\to\your\jdk\bin\java.exe" AdvancedProgrammingProject\src\project_biu\Main.java
      
         ![image](https://github.com/user-attachments/assets/cc04bd45-0c11-41c0-a05f-40e4b13cb072)


      #### When java is in Your Environment Variables
      
      Simply run the command:
      
      java AdvancedProgrammingProject\src\project_biu\Main.java
      
      ![image](https://github.com/user-attachments/assets/e718a0bf-0c39-4413-9c27-1f54becc3587)

## Usage
After running the project a tab in the browser will pop.

![image](https://github.com/user-attachments/assets/e201bc08-47ce-4eae-ac9d-46b639989346)

### Choose Configuration File:
Use the "Choose File" button to select a configuration file from your local system.

Once selected, click the "Deploy" button to upload and visualize the graph.

### Send Messages on Topics:

 After uploading the configuration file you can send messages to specific topics by entering the topic name and the message, then clicking the "Send" button.

 A table containing the updated values of topics will be shown on the side.

## Configuration Files

Configuration files define the blocks to create, with each block representing an agent. Each block contains:

- The Agent's Class

- Topics for which the agent subscribes

- One topic that the agent publishes to


#### Agent Classes
- **IncAgent**: Receives 1 topic input.
- **PlusAgent**: Receives 2 topic inputs.
- **ExponentAgent**: Receives 2 topic inputs.
- **MulAgent**: Receives 2 topic inputs.
- **DivAgent**: Receives 2 topic inputs.

#### Notes:
- When including an Agent class please note the right path project_biu.configs.AgentClass.

- Make sure your files doesn't contain empty lines or uneccesary spaces.

- Number of lines in file should divide by 3.

### Configuration File Example:

![image](https://github.com/user-attachments/assets/fb3240f1-a01d-4e3b-b12e-d0210a9fda7d)

## Features

#### View Agent Node Equation:

Press a node to view its equation.

#### View All Equations:

Push the Show Equations button to view the equations and results for all node.

#### Zoom In/Out:

Use mouse to zoom in/out graph.

#### Rearrange graph:

Drag nodes to rearrange the graph.








