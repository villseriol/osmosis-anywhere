import {
    MAX_LATITUDE,
    drawArrow,
    drawBoxes,
    drawPoint,
    offsetBetween,
    renderVariant,
    shiftBox,
    squareAround,
    wrapLatitude,
} from '@/coordinate-bounds-template'

const FROM = { latitude: 34, longitude: 135 }
const TO = { latitude: 85, longitude: 70 }
const SHIFTED = squareAround(TO)

renderVariant('generate-bounds-latitude-within', (figure) => {
    drawPoint(figure, FROM)
    drawBoxes(figure, shiftBox(squareAround(FROM), offsetBetween(FROM, TO)), {
        color: 'blue',
        shifted: true,
    })
    drawBoxes(
        figure,
        [
            {
                west: SHIFTED.west,
                east: SHIFTED.east,
                south: -MAX_LATITUDE,
                north: wrapLatitude({
                    latitude: SHIFTED.north,
                    longitude: TO.longitude,
                }).latitude,
            },
        ],
        { color: 'blue', shifted: true }
    )
    drawPoint(figure, TO, { labelSide: 'right' })
    drawArrow(figure, FROM, TO)
})
