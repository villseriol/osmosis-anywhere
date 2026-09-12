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
const TO = { latitude: 100, longitude: 70 }
const SHIFTED = squareAround(TO)

renderVariant('generate-bounds-latitude-invalid', (figure) => {
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
    drawPoint(figure, FROM)
    drawPoint(figure, TO, { labelSide: 'right' })
    drawArrow(figure, FROM, TO)
    drawPoint(figure, wrapLatitude(TO), {
        color: 'blue',
        boxes: [],
        labelSide: 'right',
        labelOffset: 40,
    })
})
