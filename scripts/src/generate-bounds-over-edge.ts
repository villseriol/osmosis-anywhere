import {
    drawArrow,
    drawPoint,
    offsetBetween,
    renderVariant,
    shiftBox,
    squareAround,
    wrapCoordinate,
} from '@/coordinate-bounds-template'

const FROM = { latitude: 34, longitude: 135 }
const TO = { latitude: -34, longitude: 190 }

renderVariant('generate-bounds-over-edge', (figure) => {
    drawPoint(figure, FROM)
    drawPoint(figure, TO)
    drawArrow(figure, FROM, TO)
    drawPoint(figure, wrapCoordinate(TO), {
        color: 'blue',
        boxes: shiftBox(squareAround(FROM), offsetBetween(FROM, TO)),
        shifted: true,
        labelSide: 'right',
    })
})
