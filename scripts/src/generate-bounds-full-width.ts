import {
    drawArrow,
    drawBoxes,
    drawPoint,
    MAX_LONGITUDE,
    offsetBetween,
    renderVariant,
    shiftBox,
    wrapCoordinate,
} from '@/coordinate-bounds-template'

const FROM = { latitude: 34, longitude: 135 }
const TO = { latitude: -34, longitude: 190 }
const BOUND = { west: -MAX_LONGITUDE, east: MAX_LONGITUDE, south: 9, north: 59 }

renderVariant('generate-bounds-full-width', (figure) => {
    drawBoxes(figure, [BOUND])
    drawBoxes(figure, shiftBox(BOUND, offsetBetween(FROM, TO)), {
        color: 'blue',
        shifted: true,
    })
    drawPoint(figure, FROM, { boxes: [] })
    drawPoint(figure, TO, { boxes: [], labelSide: 'right' })
    drawArrow(figure, FROM, TO)
    drawPoint(figure, wrapCoordinate(TO), {
        color: 'blue',
        boxes: [],
        labelSide: 'right',
    })
})
