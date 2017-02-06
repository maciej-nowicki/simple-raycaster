# simple-raycaster
Java implementation of simple fpp engine using raycasting technique.

This simple engine uses the same technique, which was used in famous Wolfenstein 3D, with some effects added by me. 
Raycasting is known to be very fast, so Wolf worked fine on 286/386 in 320 x 200. 
My engine is written 100% in Java and uses software rendering, so the speed... Well, it is still 50 fps in 640 x 400 on my Core i7 ;)

## engine features
* textured walls
* textured floor & ceiling
* look up / down ability
* shading (fog) effect
* walking effect
* static 2D sprites
* texture bilinear filtering
* different floor & ceiling textures
* outside areas with different landsape projections
* simple shaders concept with sample shaders
 * rain effect in outside areas

## plans (in order of priority)

* animated sprites
* moving sprites
* opacity
* mipmapping 
* thin walls
* lightning effecects
* more than one texture on wall (like in DOOM)
* configurable controls, display, etc.
* clean code
* optimizations


## controls

* arrow keys - movement
* CTRL - shoot
* Q - look up
* Z - look down
* A - center look
* W - toggle wall rendering method
* F - toggle floor/ceiling rendering method
* E - shading on/off
* H - toggle landsape rendering mode
* S - walking effect on/off
* X - sprites on/off
* T - texture bilinear filtering on/off (warning: slow!)
* G - weapon on/off
* D - debug info on screen and console on/off
* ESC - quit
* ENTER - toggle "fullscreen" mode

## credits

During implementation, I used information from following sites:

* http://lodev.org/cgtutor/raycasting.html - raycasting tutorial

## copyright
Textures from Wolfenstein 3D and graphics from DOOM (shotgun) were used only for testing purposes.
I do not own or claim any copyrights to these files.
