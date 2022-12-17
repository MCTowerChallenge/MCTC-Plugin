# Quest Management

Quests are stored per-team, with individual progress, completion, and rewards.

Players progress in quests generally by talking to specific [NPCs](entities/NPC.java), 
or interacting with [ItemEntities](entities/ItemEntityHandler.java).

NPCs interact with players using [Dialogues](Dialogue.java), which take in a function to be run when it's done.
Dialogues can also play sounds, either from vanilla minecraft or a custom resource pack like we use.