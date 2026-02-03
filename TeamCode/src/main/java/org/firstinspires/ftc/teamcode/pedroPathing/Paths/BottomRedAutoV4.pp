{
  "startPoint": {
    "x": 88.91211401425178,
    "y": 9.254156769596198,
    "heading": "linear",
    "startDeg": 90,
    "endDeg": 0,
    "locked": false
  },
  "lines": [
    {
      "id": "line-1k2p2tl8xia",
      "name": "Start to shoot1",
      "endPoint": {
        "x": 82.98337292161523,
        "y": 23.914489311163898,
        "heading": "linear",
        "startDeg": 90,
        "endDeg": 90
      },
      "controlPoints": [],
      "color": "#955CA9",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "ml2sr96x-vjpz4y",
      "name": "Shoot1 to pickup1",
      "endPoint": {
        "x": 136.53444180522564,
        "y": 26.529691211401435,
        "heading": "linear",
        "reverse": false,
        "startDeg": 90,
        "endDeg": -90
      },
      "controlPoints": [
        {
          "x": 113.21496437054634,
          "y": 8.585510688836115
        }
      ],
      "color": "#D98DD5",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "ml2suw0q-62ze5u",
      "name": "Pickup1 to intake1",
      "endPoint": {
        "x": 136.3705463182898,
        "y": 9.627078384798088,
        "heading": "linear",
        "reverse": false,
        "startDeg": -90,
        "endDeg": -90
      },
      "controlPoints": [],
      "color": "#D68D59",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "ml2svw23-qz2ed0",
      "name": "Intake1 to shoot2",
      "endPoint": {
        "x": 82.92874109263659,
        "y": 23.91923990498814,
        "heading": "linear",
        "reverse": false,
        "startDeg": -90,
        "endDeg": 90
      },
      "controlPoints": [
        {
          "x": 110.1935866983373,
          "y": 21.343230403800472
        }
      ],
      "color": "#87A8BC",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    },
    {
      "id": "ml2sxr7o-vwact8",
      "name": "Shoot2 to park",
      "endPoint": {
        "x": 108.6603325415677,
        "y": 11.16389548693586,
        "heading": "linear",
        "reverse": false,
        "startDeg": 90,
        "endDeg": 90
      },
      "controlPoints": [
        {
          "x": 95.16270783847982,
          "y": 13.332541567695973
        }
      ],
      "color": "#5C659D",
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": ""
    }
  ],
  "shapes": [
    {
      "id": "triangle-1",
      "name": "Red Goal",
      "vertices": [
        {
          "x": 0,
          "y": 70
        },
        {
          "x": 0,
          "y": 144
        },
        {
          "x": 24,
          "y": 144
        },
        {
          "x": 6,
          "y": 119
        },
        {
          "x": 6,
          "y": 70
        }
      ],
      "color": "#dc2626",
      "fillColor": "#ff6b6b"
    },
    {
      "id": "triangle-2",
      "name": "Blue Goal",
      "vertices": [
        {
          "x": 138,
          "y": 119
        },
        {
          "x": 119,
          "y": 144
        },
        {
          "x": 144,
          "y": 144
        },
        {
          "x": 144,
          "y": 70
        },
        {
          "x": 137,
          "y": 70
        }
      ],
      "color": "#2563eb",
      "fillColor": "#60a5fa"
    }
  ],
  "sequence": [
    {
      "kind": "path",
      "lineId": "line-1k2p2tl8xia"
    },
    {
      "kind": "path",
      "lineId": "ml2sr96x-vjpz4y"
    },
    {
      "kind": "path",
      "lineId": "ml2suw0q-62ze5u"
    },
    {
      "kind": "path",
      "lineId": "ml2svw23-qz2ed0"
    },
    {
      "kind": "path",
      "lineId": "ml2sxr7o-vwact8"
    }
  ],
  "settings": {
    "xVelocity": 75,
    "yVelocity": 65,
    "aVelocity": 3.141592653589793,
    "kFriction": 0.5,
    "rWidth": 18,
    "rHeight": 14,
    "safetyMargin": 1,
    "maxVelocity": 70,
    "maxAcceleration": 30,
    "maxDeceleration": 30,
    "fieldMap": "decode.webp",
    "robotImage": "/robot.png",
    "theme": "auto",
    "showGhostPaths": false,
    "showOnionLayers": false,
    "onionLayerSpacing": 4,
    "onionColor": "#17d3a4",
    "onionNextPointOnly": false
  },
  "version": "1.2.1",
  "timestamp": "2026-01-31T21:13:22.443Z"
}