{
  "startPoint": {
    "x": 55.08788598574822,
    "y": 9.254156769596198,
    "heading": "linear",
    "startDeg": 90,
    "endDeg": 180,
    "locked": false
  },
  "lines": [
    {
      "id": "line-1k2p2tl8xia",
      "name": "Start to shoot1",
      "endPoint": {
        "x": 61.01662707838477,
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
        "x": 7.465558194774347,
        "y": 26.529691211401435,
        "heading": "linear",
        "reverse": false,
        "startDeg": 90,
        "endDeg": 270
      },
      "controlPoints": [
        {
          "x": 30.785035629453667,
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
        "x": 7.629453681710213,
        "y": 9.627078384798088,
        "heading": "linear",
        "reverse": false,
        "startDeg": 270,
        "endDeg": 270
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
        "x": 61.071258907363415,
        "y": 23.91923990498814,
        "heading": "linear",
        "reverse": false,
        "startDeg": 270,
        "endDeg": 90
      },
      "controlPoints": [
        {
          "x": 33.80641330166271,
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
        "x": 35.3396674584323,
        "y": 11.16389548693586,
        "heading": "linear",
        "reverse": false,
        "startDeg": 90,
        "endDeg": 90
      },
      "controlPoints": [
        {
          "x": 48.83729216152018,
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
          "x": 144,
          "y": 70
        },
        {
          "x": 144,
          "y": 144
        },
        {
          "x": 120,
          "y": 144
        },
        {
          "x": 138,
          "y": 119
        },
        {
          "x": 138,
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
          "x": 6,
          "y": 119
        },
        {
          "x": 25,
          "y": 144
        },
        {
          "x": 0,
          "y": 144
        },
        {
          "x": 0,
          "y": 70
        },
        {
          "x": 7,
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
  "timestamp": "2026-01-31T21:07:36.871Z"
}