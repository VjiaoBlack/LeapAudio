window.lHandy = 0;
window.volume = 0;


window.note = {};
window.note["C3"] = T("sin", {freq:130.81, mul:.6});
window.note["D3"] = T("sin", {freq:146.83, mul:.6});
window.note["E3"] = T("sin", {freq:164.81, mul:.6});
window.note["F3"] = T("sin", {freq:174.61, mul:.6});
window.note["G3"] = T("sin", {freq:196.00, mul:.6});
window.note["A3"] = T("sin", {freq:220.00, mul:.6});
window.note["B3"] = T("sin", {freq:246.94, mul:.6});
window.note["C4"] = T("sin", {freq:261.63, mul:.55});
window.note["D4"] = T("sin", {freq:293.66, mul:.55});
window.note["E4"] = T("sin", {freq:329.63, mul:.3});
window.note["F4"] = T("sin", {freq:349.23, mul:.3});
window.note["G4"] = T("sin", {freq:392.00, mul:.3});
window.note["A4"] = T("sin", {freq:440.00, mul:.3});
window.note["B4"] = T("sin", {freq:493.88, mul:.25});
window.note["C5"] = T("sin", {freq:523.25, mul:.25});

window.chords = {};
window.chords[1] = T("+", note["C3"], note["E4"], note["G4"]).set({mul: 1});
window.chords[2] = T("+", note["D3"], note["F4"], note["A4"]).set({mul: 1});
window.chords[3] = T("+", note["E3"], note["G4"], note["B4"]).set({mul: 1});
window.chords[4] = T("+", note["F3"], note["A4"], note["C5"]).set({mul: 1});
window.chords[5] = T("+", note["G3"], note["B4"], note["D4"]).set({mul: 1});
window.chords[6] = T("+", note["A3"], note["C4"], note["E4"]).set({mul: 1});
window.chords[7] = T("+", note["B3"], note["D4"], note["F4"]).set({mul: 1});
window.chords[8] = T("+", note["C4"], note["E4"], note["G4"]).set({mul: 1});


window.asdf = 1;
function toggle() {
    if (window.asdf == null) {
        window.asdf = setInterval(testFingers, 50);
        window.numfingers = 0;
        window.currnumf = 0;
        window.volume = 0;
    } else {
        window.clearInterval(window.asdf);
        window.asdf = null;
    }
    console.log("wt");
}



window.rnumf = document.getElementById("right");
window.vol = document.getElementById("vol");
document.getElementById("stopstart").addEventListener("click", toggle); //= document.getElementById("stopstart");

window.currnumf = 0;
window.oldrnumf = 0;
// window.lnumf = document.getElementById("left");




window.chord = window.chords[1];

function numFingers(fingers) {
    var thumb = fingers[0].extended;
    var index = fingers[1].extended;
    var middle = fingers[2].extended;
    var ring = fingers[3].extended;
    var pinky = fingers[4].extended;

    var output = document.getElementById('output'),
    progress = document.getElementById('progress');


    if (thumb) {
        if (index && middle && ring && pinky) {
            return 5;
        } else if (index && middle) {
            return 8;
        } else if (index) {
            return 7;
        } else {
            return 6;
        }
    } else if (index) {
        if (middle && ring && pinky) {
          return 4;
      } else if (index && ring) {
          return 3;
      } else if (middle) {
          return 2;
      } else {
          return 1;
      }
    } else {
      return 0;
    }
}


function testFingers() {
    if (window.lHand) {

        window.lHandy = window.lHand.palmPosition[1];

        window.volume = Math.round(((800 - window.lHandy) / 400 - .5) * 100) / 100;


        window.oldrnumf = window.currnumf;
        var  i = 0;
        var fingers = window.lHand.fingers;
        var numfingers = numFingers(fingers);
    } else {
        numfingers = 0;
        window.volume = 0;
    }
    window.currnumf = numfingers;
    if (window.currnumf == 0) {
        window.volume = 0;
        window.chord.set({mul: window.volume});
    }

    switch (numfingers) {
        case 1:
        window.rnumf.innerHTML = "I - C";
        break;
        case 2:
        window.rnumf.innerHTML = "ii - d";
        break;
        case 3:
        window.rnumf.innerHTML = "iii - e";
        break;
        case 4:
        window.rnumf.innerHTML = "IV - F";
        break;
        case 5:
        window.rnumf.innerHTML = "V - G";
        break;
        case 6:
        window.rnumf.innerHTML = "vi - a";
        break;
        case 7:
        window.rnumf.innerHTML = "vii - b";
        break;
        case 8:
        window.rnumf.innerHTML = "I^ - C";
        break;
        case 0:
        window.rnumf.innerHTML = "off";
        break;

    }
    // window.rnumf.innerHTML = numfingers;
    window.vol.innerHTML = window.volume;

    if (numfingers > 0) {
        if (window.currnumf != window.oldrnumf) {
            window.chord.pause();
        }

        window.chord = window.chords[numfingers].set({mul: window.volume});

        if (window.currnumf != window.oldrnumf) {
            window.chord.play();
        } else if (numfingers == 0) {
            window.chord.pause();
        }

    }
}


// Set up the controller:
window.controller = Leap.loop({background: true}, function(frame){
    if (frame.hands.length > 0) {
        output.innerHTML = frame.hands[0].confidence.toPrecision(2);
        progress.style.width = frame.hands[0].confidence * 100 + '%';
        window.lHand = frame.hands[0];

        if (asdf == 1) {
            asdf = setInterval(testFingers, 50);
        }

    } if (frame.hands.length == 2) {
        document.getElementById("righthand").innerHTML = "TWO HAND!";

        window.rHand = frame.hands[1];
    } else {
        window.rHand = null;
        document.getElementById("righthand").innerHTML = "one hand";
    }

});





    /*********************************************************
  * The rest of the code is here for visualizing the example. Feel
  * free to remove it to experiment with the API value only
  ****************************************************/

  // Adds the rigged hand and playback plugins
  // to a given controller, providing a cool demo.
  visualizeHand = function(controller){
    // The leap-plugin file included above gives us a number of plugins out of the box
    // To use a plugins, we call `.use` on the controller with options for the plugin.
    // See js.leapmotion.com/plugins for more info

    controller.use('playback').on('riggedHand.meshAdded', function(handMesh, leapHand){
      handMesh.material.opacity = 1;
    });

  var overlay = controller.plugins.playback.player.overlay;
  overlay.style.right = 0;
  overlay.style.left = 'auto';
  overlay.style.top = 'auto';
  overlay.style.padding = 0;
  overlay.style.bottom = '13px';
  overlay.style.width = '180px';


  controller.use('riggedHand', {
    scale: 1,
    boneColors: function (boneMesh, leapHand){
      if ((true) ) {
          return { // edit to do color by chords.
            hue: 0.564,
            saturation: leapHand.confidence,
            lightness: 0.5
        }
    }
}
});

  var camera = controller.plugins.riggedHand.camera;
  camera.position.set(0,20,-25);
  camera.lookAt(new THREE.Vector3(0,3,0));
};
visualizeHand(Leap.loopController);
