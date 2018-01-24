# CurrenSee <img src="https://i.imgur.com/bSM2r5R.png" height="50" width="50">
## A currency counter for the visually impaired.

### Why?
<img src="https://i.imgur.com/XZjIWg6.jpg" height="320" width="480">

We saw that the new notes introduced after demonetisation were not disablility friendly. We wanted to try and make an app that made it easier for the visually disabled to be able to count money fast and efficiently.

### The Inner Workings.
The app is very simple to use, and designed with the visually impaired in mind. The GUI is extremely simple. All the user has to do is hold up the money and take a picture of the notes by tapping anywhere on the screen. The phone **vibrates** when the picture is taken, after which this picture is sent to our servers (Azure VM), which process the image and send the value back to the app. This value is then spoken aloud back to the user.

### Demo

Image Capture By Phone            |  Note Detection
:-------------------------:|:-------------------------:
<img src="https://i.imgur.com/q8C4vGB.jpg" height="450" width="240">  |  <img src="https://i.imgur.com/ZKcj1bo.jpg" height="450" width="240">

Note Detection             | Note Detection
:-------------------------:|:-------------------------:
<img src="https://i.imgur.com/WFtuArv.jpg" height="450" width="240">  |  <img src="https://i.imgur.com/JWRfr2w.jpg" height="450" width="240">
  ## Output Calculated by the program: Rs 2700
  ## [Video Demo](https://youtu.be/a-TqUBzutUk)
### Future improvements
Since this app was concieved in a hackathon, we had to deal with a lot of time constraints. As a result, the this iteration supports few notes, Rs 500, Rs 2000, Rs 200 and Rs 50. Also, the note detection capability can be switched over to an R-CNN for improved note detection, which only gets better over time. Due to lack of a proper training corpus, and time, such a network could not be trained/implemented.

**Originally we had envisioned this to be a virtual wallet of sorts, which keeps track of any and all currency notes and can integrate with third party services too. This way, the visually impaired would never have to fidget around with their wallets to count money.**
